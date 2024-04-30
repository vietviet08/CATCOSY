package com.dacs1.library.service;

import com.dacs1.library.dto.CustomerDto;
import com.dacs1.library.model.Customer;

import java.util.Optional;

public interface CustomerService {
    Customer findByUsername(String username);

    CustomerDto findByUsernameDto(String username);

    Customer save(CustomerDto customerDto);

    Customer updateCustomer(Customer customer);

    Customer processOAuthLogin(String username, String email);


    CustomerDto findByResetPasswordToken(String token);

    void updateResetPasswordToken(String email, String token);

    void updatePassword(CustomerDto customer, String password);
}
