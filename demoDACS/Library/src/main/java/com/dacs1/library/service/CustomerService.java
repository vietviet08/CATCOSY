package com.dacs1.library.service;

import com.dacs1.library.dto.CustomerDto;
import com.dacs1.library.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<CustomerDto> getAllCustomer();

    Customer findByUsername(String username);

    CustomerDto finByUsernameIsActive(String username);

    CustomerDto findByUsernameDto(String username);

    CustomerDto findByEmail(String email, String provider);

    Customer save(CustomerDto customerDto);

    Customer updateCustomer(Customer customer);

    Customer processOAuthLogin(String username, String email);

    Customer findByResetPasswordToken(String token);

    Customer updateResetPasswordToken(String email, String token);

    void updatePassword(Customer customer, String password);


    Customer lockCustomer(String username);

    Customer unlockCustomer(String username);

    Customer deleteCustomer(String username);
}
