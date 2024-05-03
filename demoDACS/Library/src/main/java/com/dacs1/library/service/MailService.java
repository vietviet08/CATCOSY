package com.dacs1.library.service;

import com.dacs1.library.dto.CustomerDto;
import com.dacs1.library.model.Customer;
import com.dacs1.library.model.Order;
import com.dacs1.library.model.Voucher;

public interface MailService {

    void testSendMail();

    void sendMailToCustomer(CustomerDto customerDto);

    void sendMailOrderToCustomer(Customer customer, Order order);

    void sendMaiResetPasswordToCustomer(String email, String linkReset);

    String sendMailVoucherToCustomer(String email, Voucher voucher);
}
