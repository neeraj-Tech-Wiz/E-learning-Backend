package com.elearning.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendWarningEmail(String to, String studentName,
                                 int percentage, int month, int year) {

        String subject = "⚠️ Attendance Warning";

        String body = String.format(
                "Dear %s,\n\n" +
                        "Your attendance for %d/%d has fallen below the required threshold.\n" +
                        "Current attendance: %d%%.\n\n" +
                        "Please improve your attendance to avoid further action.\n\n" +
                        "Regards,\nSchool Administration",
                studentName, month, year, percentage
        );

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("elearningplatformedu@gmail.com");

        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}