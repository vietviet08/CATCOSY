package com.dacs1.library.service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailSenderConfig {


    @Bean
    public JavaMailSender getJavaMailSender(){
        JavaMailSenderImpl  javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setPort(587);
        javaMailSender.setUsername("vietnq.23ceb@vku.udn.vn");
        javaMailSender.setPassword("vkdwgrcpqrmigwuv");

        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.debug", "true");


        javaMailSender.setJavaMailProperties(properties);

        javaMailSender.setDefaultEncoding("utf-8");
        javaMailSender.setProtocol("smtp");

        return javaMailSender;
    }
}
