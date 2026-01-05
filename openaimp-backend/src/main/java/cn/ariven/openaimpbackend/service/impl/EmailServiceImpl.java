package cn.ariven.openaimpbackend.service.impl;

import cn.ariven.openaimpbackend.service.IEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("【注册/修改密码验证码】");
        message.setText("您的验证码是：" + code + "，有效期1分钟，请尽快使用。");

        mailSender.send(message);
    }

    @Override
    public void sendRatingUpdateEmail(String to, String messageText) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("【Rating更改通知】");
        message.setText("您的Rating已经更新为" + messageText);
        mailSender.send(message);
    }
}
