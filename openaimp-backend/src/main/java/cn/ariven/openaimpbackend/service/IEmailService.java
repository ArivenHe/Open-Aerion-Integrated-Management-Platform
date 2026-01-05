package cn.ariven.openaimpbackend.service;

public interface IEmailService {
    void sendVerificationCode(String to, String code);
    void sendRatingUpdateEmail(String email, String ratingText);
}
