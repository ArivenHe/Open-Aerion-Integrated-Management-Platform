package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.dto.request.auth.RequestLogin;
import cn.ariven.openaimpbackend.dto.request.auth.RequestRegister;
import cn.ariven.openaimpbackend.dto.request.auth.RequestResetPassword;
import cn.ariven.openaimpbackend.dto.request.openfsd.CreateUserRequest;
import cn.ariven.openaimpbackend.dto.response.openfsd.UserInfo;
import cn.ariven.openaimpbackend.pojo.Role;
import cn.ariven.openaimpbackend.pojo.User;
import cn.ariven.openaimpbackend.repository.RoleRepository;
import cn.ariven.openaimpbackend.repository.UserRepository;
import cn.ariven.openaimpbackend.service.CommonService;
import cn.ariven.openaimpbackend.service.OpenFsdApiService;
import cn.ariven.openaimpbackend.service.UserService;
import cn.ariven.openaimpbackend.util.CodeUtil;
import cn.ariven.openaimpbackend.util.PasswordEncoder;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;
    private final OpenFsdApiService openFsdApiService;
    private final CommonService commonService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final String RESET_CODE_PREFIX = "reset_code:";
    private static final String REGISTER_CODE_PREFIX = "register_code:";
    private static final String CAPTCHA_PREFIX = "captcha:";
    private static final int OBSERVER_RATING = 1;

    @Override
    @Transactional
    public void register(RequestRegister request) {
        validateCaptcha(request.getCaptchaKey(), request.getCaptchaCode());
        validateRegisterCode(request.getEmail(), request.getEmailCode());

        if (userRepository.existsByCallsign(request.getCallsign())) {
            throw new RuntimeException("Callsign already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setCallsign(request.getCallsign());
        user.setPassword(PasswordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setQq(request.getQq());
        Role defaultRole = roleRepository.findByName("user");
        if (defaultRole == null) {
            throw new RuntimeException("Default role not found");
        }
        user.setRoles(List.of(defaultRole));

        //注册FSD用户
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .password(user.getPassword())
                .firstName(user.getCallsign())
                .lastName(user.getEmail())
                .networkRating(OBSERVER_RATING)
                .build();


        UserInfo userInfo = openFsdApiService.createUser(createUserRequest, commonService.getFsdAccessTokenBySystemAdmin());
        if (userInfo != null) {
            log.info("FSD用户注册成功，开始注册飞控用户");
            userRepository.save(user);
            log.info("注册完成");
        } else {
            log.info("FSD用户注册失败");
        }

    }

    @Override
    public String login(RequestLogin request) {
        validateCaptcha(request.getCaptchaKey(), request.getCaptchaCode());

        Optional<User> userOpt = userRepository.findByCallsign(request.getAccount());
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(request.getAccount());
        }

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        if (!PasswordEncoder.compare(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        StpUtil.login(user.getId());
        return StpUtil.getTokenValue();
    }


    private void validateCaptcha(String key, String code) {
        String cachedCode = redisTemplate.opsForValue().get(CAPTCHA_PREFIX + key);
        if (cachedCode == null || !cachedCode.equalsIgnoreCase(code)) {
            throw new RuntimeException("Invalid or expired captcha");
        }
        redisTemplate.delete(CAPTCHA_PREFIX + key);
    }

    private void validateRegisterCode(String email, String code) {
        String cachedCode = redisTemplate.opsForValue().get(REGISTER_CODE_PREFIX + email);
        if (cachedCode == null || !cachedCode.equals(code)) {
            throw new RuntimeException("Invalid or expired email code");
        }
        redisTemplate.delete(REGISTER_CODE_PREFIX + email);
    }

    @Override
    public void sendRegisterCode(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        String code = CodeUtil.generateCode(6);
        redisTemplate.opsForValue().set(REGISTER_CODE_PREFIX + email, code, Duration.ofMinutes(15));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Register Verification Code");
        message.setText("Your verification code is: " + code + "\nValid for 15 minutes.");

        mailSender.send(message);
    }

    @Override
    public void sendForgotPasswordCode(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email not found");
        }

        String code = CodeUtil.generateCode(6);
        redisTemplate.opsForValue().set(RESET_CODE_PREFIX + email, code, Duration.ofMinutes(15));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Password Reset Code");
        message.setText("Your verification code is: " + code + "\nValid for 15 minutes.");

        mailSender.send(message);
    }

    @Override
    @Transactional
    public void resetPassword(RequestResetPassword request) {
        String cachedCode = redisTemplate.opsForValue().get(RESET_CODE_PREFIX + request.getEmail());
        if (cachedCode == null || !cachedCode.equals(request.getCode())) {
            throw new RuntimeException("Invalid or expired code");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(PasswordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        redisTemplate.delete(RESET_CODE_PREFIX + request.getEmail());
    }

}
