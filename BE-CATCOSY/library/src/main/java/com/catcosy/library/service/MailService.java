package com.catcosy.library.service;

import com.catcosy.library.dto.CustomerDto;
import com.catcosy.library.model.Customer;
import com.catcosy.library.model.Order;
import com.catcosy.library.model.Voucher;

public interface MailService {

    void testSendMail();

    void sendMailToCustomer(CustomerDto customerDto);

    void sendMailOrderToCustomer(Customer customer, Order order);

    void sendMaiResetPasswordToCustomer(String email, String linkReset);

    String sendMailVoucherToCustomer(String email, Voucher voucher);
}
