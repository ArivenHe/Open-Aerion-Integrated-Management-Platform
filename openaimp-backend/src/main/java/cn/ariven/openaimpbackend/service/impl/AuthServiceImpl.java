package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.dto.ResponseMessage;
import cn.ariven.openaimpbackend.dto.UserDto;
import cn.ariven.openaimpbackend.entity.User;
import cn.ariven.openaimpbackend.repository.RegistrationRulesRepository;
import cn.ariven.openaimpbackend.repository.UserRepository;
import cn.ariven.openaimpbackend.service.IAuthService;
import cn.ariven.openaimpbackend.service.IEmailService;
import cn.ariven.openaimpbackend.util.CodeUtil;
import cn.ariven.openaimpbackend.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final IEmailService emailService;
    private final RegistrationRulesRepository registrationRulesRepository;

    @Override
    public ResponseMessage<User> addUser(UserDto user) {
        User userPojo = new User();
        BeanUtils.copyProperties(user, userPojo);
        // Assuming password encoding is handled before calling this or handled by register logic
        User savedUser = userRepository.save(userPojo);
        return ResponseMessage.success(savedUser);
    }

    @Override
    public ResponseMessage<Boolean> isEmailRegistered(String email) {
        return ResponseMessage.success(userRepository.existsByEmail(email));
    }

    @Override
    public ResponseMessage<Boolean> isCallsignRegistered(String callsign) {
        return ResponseMessage.success(userRepository.existsByCallsign(callsign));
    }

    @Override
    public ResponseMessage<Boolean> activateUser(String userId) {
        try {
            int updatedCount = userRepository.updateRatingByCid(Integer.parseInt(userId), 1);
            return updatedCount > 0 ? ResponseMessage.success(true) : ResponseMessage.error("Activation failed");
        } catch (NumberFormatException e) {
            return ResponseMessage.error("Invalid user ID");
        }
    }

    @Override
    public ResponseMessage<Boolean> login(String email, String password) {
        log.info("Login attempt for email: {}", email);
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            log.warn("No user found for email: {}", email);
            return ResponseMessage.error("User not found");
        }
        boolean isValid = PasswordEncoder.compare(password, user.getPassword());
        log.info("Password validation result: {}", isValid);

        if (isValid) {
            return ResponseMessage.success(true);
        } else {
            return ResponseMessage.error("Invalid credentials");
        }
    }

    @Override
    public ResponseMessage<Boolean> forgotPassword(String email, String password) {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            log.warn("No user found for email: {}", email);
            return ResponseMessage.error("User not found");
        } else {
            user.setPassword(PasswordEncoder.encode(password));
            userRepository.save(user);
            return ResponseMessage.success(true);
        }
    }

    @Override
    public ResponseMessage<Boolean> regBbs(String callsign, String password, String email) {
        try {
            // TODO: Move base URL to config
            // String baseUrl = "http://bbs.cloudswoop.icu/plugin.php";
            // Implementation pending or not fully migrated in previous steps
            return ResponseMessage.success(true);
        } catch (Exception e) {
            return ResponseMessage.error("BBS registration failed: " + e.getMessage());
        }
    }

    @Override
    public void storeUserToRedis(int userId, User userDetail) {
        // Implementation logic if needed
    }

    @Override
    public ResponseMessage<String> sendEmailCode(String clientIp, String email) {
        String ipKey = "email_ip_limit:" + clientIp;

        Boolean exists = redisTemplate.hasKey(ipKey);
        if (Boolean.TRUE.equals(exists)) {
            return ResponseMessage.error("请求过于频繁，请稍后再试");
        }

        String code = CodeUtil.generateCode(6);
        String key = "email_code:" + email;

        redisTemplate.opsForValue().set(key, code, 1, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(ipKey, "1", 1, TimeUnit.MINUTES);

        emailService.sendVerificationCode(email, code);

        return ResponseMessage.success("发送成功");
    }

    @Override
    public ResponseMessage<String> register(UserDto user) {
        String key = "email_code:" + user.getEmail();
        log.info(key);
        String cachedCode = redisTemplate.opsForValue().get(key);
        log.info(cachedCode);
        log.info(user.getCode());
        log.info(user.getCallsign());

        if (!user.getCallsign().matches("\\d{4}")) {
            return ResponseMessage.error("呼号必须是4位数字");
        }
        if (registrationRulesRepository.existsById(user.getCallsign())){
            return ResponseMessage.error("禁止使用此呼号进行注册！");
        }

        if (cachedCode == null || !cachedCode.equals(user.getCode())) {
            return ResponseMessage.error("验证码错误或已过期");
        }

        log.info("注册开始");
        log.info(user.toString());

        boolean emailExists = userRepository.existsByEmail(user.getEmail());
        boolean callsignExists = userRepository.existsByCallsign(user.getCallsign());

        if (!emailExists && !callsignExists) {
            user.setPassword(PasswordEncoder.encode(user.getPassword()));
            
            // Logic from Controller to Service
            User userPojo = new User();
            BeanUtils.copyProperties(user, userPojo);
            User savedUser = userRepository.save(userPojo);

            if (savedUser != null) {
                log.info("注册成功: {}", user.getEmail());
                String activationToken = UUID.randomUUID().toString();
                log.info("activationToken: {}", activationToken);
                return ResponseMessage.success(activationToken);
            }
        }
        return ResponseMessage.error("注册失败");
    }
}
