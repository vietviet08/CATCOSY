package com.catcosy.library.service.impl;

import com.catcosy.library.dto.CustomerDto;
import com.catcosy.library.dto.OrderDetailDto;
import com.catcosy.library.model.Customer;
import com.catcosy.library.model.Order;
import com.catcosy.library.model.Voucher;
import com.catcosy.library.service.*;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.*;

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

    @Autowired
    private CustomerService customerService;

    @Autowired
    private VoucherService voucherService;

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
            variable.put("idOrder", order.getId());
            variable.put("products", orderDetailList);

            String subPrice = formatCurrency(order.getTotalPrice() + order.getDiscountPrice() + order.getShippingFee());
            variable.put("subPrice", subPrice);

            String totalPrice = formatCurrency(order.getTotalPrice());
            variable.put("totalPrice", totalPrice);

            String salePrice = formatCurrency(order.getDiscountPrice());
            variable.put("salePrice", salePrice);

            String shippingFee = formatCurrency(order.getShippingFee());
            variable.put("shippingFee", shippingFee);
            variable.put("paymentMethod", order.getPaymentMethod());
            variable.put("deliveryAddress", order.getDeliveryAddress());
            variable.put("codeViewOrder", order.getCodeViewOrder());

            helper.setText(thymeleafService.createContent("mail-ordered-customer1", variable), true);
            helper.setSubject("Your order in CATCOSY");
            javaMailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMaiResetPasswordToCustomer(String emailCustomer, String linkReset) {
        try {

            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(email);
            helper.setTo(emailCustomer);

            Map<String, Object> variable = new HashMap<>();
            variable.put("email", emailCustomer);
            variable.put("linkReset", linkReset);

            helper.setText(thymeleafService.createContent("mail-reset-password-customer", variable), true);
            helper.setSubject("Reset password in CATCOSY");
            javaMailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String sendMailVoucherToCustomer(String emailCustomer, Voucher voucher) {
        try {
            CustomerDto customer = customerService.findByEmail(email);
            if (customer == null) return "Not found customer, email not registered!";

            if (voucher.isUsed() || !voucher.getExpiryDate().after(new Date()) || !voucher.getForEmailCustomer().isEmpty())
                return "Voucher has been used or expired or is reserved for a certain customer!";

            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(email);
            helper.setTo(emailCustomer);

            Map<String, Object> variable = new HashMap<>();
            variable.put("email", emailCustomer);
            variable.put("voucher", voucher);


            helper.setText(thymeleafService.createContent("mail-voucher-customer", variable), true);
            helper.setSubject("Your voucher in CATCOSY");

            voucher.setForEmailCustomer(emailCustomer);
            voucher.setUsageLimits(1);
            voucherService.saveVoucher(voucher);

            javaMailSender.send(message);
            return "Send voucher to customer successfully!";

        } catch (Exception e) {
            e.printStackTrace();
            return "Send mail error maybe error from server!";
        }
    }

    public String formatCurrency(double amount) {
        Locale vietnam = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(vietnam);
        return currencyFormatter.format(amount);
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
