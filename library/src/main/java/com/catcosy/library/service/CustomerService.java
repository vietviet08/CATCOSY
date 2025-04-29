package com.catcosy.library.service;

import com.catcosy.library.dto.CustomerDto;
import com.catcosy.library.model.Admin;
import com.catcosy.library.model.Customer;

import java.util.List;

public interface CustomerService {
    List<Customer> getAll();

    List<CustomerDto> getAllCustomer();

    Customer findByUsername(String username);

    CustomerDto finByUsernameIsActive(String username);

    CustomerDto findByUsernameDto(String username);

    CustomerDto findByEmailAndProvider(String email, String provider);

    CustomerDto findByEmail(String email);

    Customer save(CustomerDto customerDto);

    Customer saveByAdmin(Admin admin);

    Customer updateCustomer(Customer customer);

    Customer processOAuthLogin(String username, String email);

    Customer findByResetPasswordToken(String token);

    Customer updateResetPasswordToken(String email, String token);

    void updatePassword(Customer customer, String password);


    Customer lockCustomer(String username);

    Customer unlockCustomer(String username);

    Customer deleteCustomer(String username);
}
