package com.fashion.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendPasswordReset(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("[Fashion] Đặt lại mật khẩu");
            message.setText("""
                Xin chào,
                
                Bạn vừa yêu cầu đặt lại mật khẩu. Nhấp vào liên kết bên dưới để tiếp tục:
                
                http://localhost:3000/account/reset-password?token=%s
                
                Liên kết có hiệu lực trong 2 giờ.
                
                Nếu bạn không yêu cầu điều này, vui lòng bỏ qua email này.
                
                Trân trọng,
                Fashion Team
                """.formatted(token));
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
