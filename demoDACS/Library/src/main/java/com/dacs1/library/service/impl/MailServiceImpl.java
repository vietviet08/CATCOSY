package com.dacs1.library.service.impl;

import com.dacs1.library.service.MailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender javaMailSender;


    @Value("${spring.mail.username}")
    private String email;

    @Override
    public void testSendMail() {
        try {


        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        helper.setFrom(email);
        helper.setText("test send mail");
        helper.setTo("vie24082005@gmail.com");
        helper.setSubject("Test MailSender Spring");

        javaMailSender.send(message);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
