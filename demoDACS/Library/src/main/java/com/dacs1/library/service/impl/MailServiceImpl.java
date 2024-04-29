package com.dacs1.library.service.impl;

import com.dacs1.library.dto.CustomerDto;
import com.dacs1.library.dto.OrderDetailDto;
import com.dacs1.library.model.Customer;
import com.dacs1.library.model.Order;
import com.dacs1.library.model.OrderDetail;
import com.dacs1.library.service.MailService;
import com.dacs1.library.service.OrderDetailService;
import com.dacs1.library.service.ThymeleafService;
import com.nimbusds.jose.util.IOUtils;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private ThymeleafService thymeleafService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private OrderDetailService orderDetailService;


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

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void sendMailToCustomer(CustomerDto customerDto) {

        try {

            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );


//            InputStream imageStream = resourceLoader.getResource("static/images/logo.png").getInputStream();
//            InputStream boostrapCss = resourceLoader.getResource("static/css/bootstrap.min.css").getInputStream();
//            InputStream boostrapJs = resourceLoader.getResource("static/js/bootstrap.bundle.min.js").getInputStream();
//            InputStream indexCss = resourceLoader.getResource("static/css/index.css").getInputStream();
//            InputStream homeJs = resourceLoader.getResource("static/js/home.js").getInputStream();

//            InputStream imageStream = applicationContext.getResource("/static/images/logo.png").getInputStream();
//            InputStream boostrapCss = applicationContext.getResource("/static/css/bootstrap.min.css").getInputStream();
//            InputStream boostrapJs = applicationContext.getResource("/static/js/bootstrap.bundle.min.js").getInputStream();
//            InputStream indexCss = applicationContext.getResource("/static/css/index.css").getInputStream();
//            InputStream homeJs = applicationContext.getResource("/static/js/home.js").getInputStream();

//            InputStream imageStream = getClass().getResourceAsStream("/static/images/logo.png");
//            InputStream boostrapCss = getClass().getResourceAsStream("/static/css/bootstrap.min.css");
//            InputStream boostrapJs = getClass().getResourceAsStream("/static/js/bootstrap.bundle.min.js");
//            InputStream indexCss = getClass().getResourceAsStream("/static/css/index.css");
//            InputStream homeJs = getClass().getResourceAsStream("/static/js/home.js");

//            helper.addInline("imageName", new ByteArrayResource(readAllBytes(imageStream)), "image/png");
//            helper.addInline("boostrapCss", new ByteArrayResource(readAllBytes(boostrapCss)), "text/css");
//            helper.addInline("boostrapJs", new ByteArrayResource(readAllBytes(boostrapJs)), "text/js");
//            helper.addInline("indexCss", new ByteArrayResource(readAllBytes(indexCss)), "text/css");
//            helper.addInline("homeJs", new ByteArrayResource(readAllBytes(homeJs)), "text/js");


            helper.setFrom(email);
            helper.setTo(customerDto.getEmail());

            Map<String, Object> variable = new HashMap<>();
            variable.put("fullName", customerDto.getFirstName() + " " + customerDto.getLastName());
            variable.put("firstName", customerDto.getFirstName());
            variable.put("lastName", customerDto.getLastName());
            variable.put("username", customerDto.getUsername());
            variable.put("email", customerDto.getEmail());
            if (customerDto.getPhone() != null) variable.put("phone", customerDto.getPhone());

            helper.setText(thymeleafService.createContent("mail-registered-customer", variable), true);
            helper.setSubject("Welcome to our shop");
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMailOrderToCustomer(Customer customer, Order order) {
        try {

            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(email);
            helper.setTo(customer.getEmail());

            Map<String, Object> variable = new HashMap<>();
            variable.put("fullName", customer.getFirstName() + " " + customer.getLastName());
            variable.put("firstName", customer.getFirstName());
            variable.put("lastName", customer.getLastName());
            variable.put("username", customer.getUsername());
            variable.put("email", customer.getEmail());
            if (customer.getPhone() != null) variable.put("phone", customer.getPhone());

            List<OrderDetailDto> orderDetailList = orderDetailService.finAllByOrderIdDto(order.getId());

            variable.put("products", orderDetailList);
            variable.put("totalPrice", order.getTotalPrice());
            variable.put("salePrice", 0);
            variable.put("shippingFee", order.getShippingFee());
            variable.put("paymentMethod", order.getPaymentMethod());
            variable.put("deliveryAddress", order.getDeliveryAddress());

            helper.setText(thymeleafService.createContent("mail-ordered-customer", variable), true);
            helper.setSubject("Welcome to our shop");
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMaiOTPToCustomer(Customer customer) {

    }

    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        try {
            if (inputStream == null) {
                throw new RuntimeException("Resource not found");
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
