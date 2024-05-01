package com.dacs1.library.service.impl;

import com.dacs1.library.dto.CustomerDto;
import com.dacs1.library.enums.Provider;
import com.dacs1.library.model.Customer;
import com.dacs1.library.repository.CustomerRepository;
import com.dacs1.library.repository.RoleRepository;
import com.dacs1.library.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RoleRepository roleRepository;


    @Override
    public Customer findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    @Override
    public CustomerDto finByUsernameIsActive(String username) {
        return toDto(customerRepository.findByUsernameAndActive(username));
    }

    @Override
    public CustomerDto findByUsernameDto(String username) {
        return toDto(customerRepository.findByUsername(username));
    }

    @Override
    public CustomerDto findByEmail(String email, String provider) {
        return toDto(customerRepository.findByEmailAndProvider(email, provider));
    }


    @Override
    public Customer save(CustomerDto customerDto) {
        return customerRepository.save(toEntity(customerDto));
    }


    @Override
    public Customer updateCustomer(Customer customer) {

        return customerRepository.save(customer);
    }

    @Override
    public Customer processOAuthLogin(String username, String email) {

        Customer customer = customerRepository.findByUsername(username);
        if (customer == null) {
            Customer c = new Customer();
            c.setUsername(username);
            c.setRoles(Arrays.asList(roleRepository.findByName("CUSTOMER")));
            c.setEmail(email);
            c.setProvider(Provider.google.name());
            return customerRepository.save(customer);
        }
        return null;
    }

    @Override
    public Customer findByResetPasswordToken(String token) {
        Customer customer = customerRepository.findByResetPasswordToken(token);
        if (customer == null) return null;
        if (Objects.equals(customer.getProvider(), Provider.local.name()))
            return customer;
        else return null;
    }

    @Override
    public Customer updateResetPasswordToken(String email, String token) {
        Customer customer = customerRepository.findByEmailAndProvider(email, Provider.local.name());

        if(customer == null) return null;

        customer.setResetPasswordToken(token);

       return customerRepository.save(customer);

    }

    @Override
    public void updatePassword(Customer customer, String password) {
        Customer customer1 = customerRepository.getReferenceById(customer.getId());
        customer1.setPassword(password);
        customer1.setResetPasswordToken(null);
        customerRepository.save(customer1);
    }

    private CustomerDto toDto(Customer customer) {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setLastName(customer.getLastName());
        customerDto.setUsername(customer.getUsername());
        customerDto.setPassword(customer.getPassword());
        customerDto.setCity(customer.getCity());
        customerDto.setPhone(customer.getPhone());
        customerDto.setEmail(customer.getEmail());
//        customerDto.setBirthDay(customer.getBirthDay());
        if (customer.getSex() != null) if (customer.getSex() == 0) customerDto.setSex("Female");
        else customerDto.setSex("Male");
        customerDto.setProvider(customerDto.getProvider());
        customerDto.setResetPasswordToken(customer.getResetPasswordToken());
        customerDto.setActive(customer.isActive());

        return customerDto;
    }

    private Customer toEntity(CustomerDto customerDto) {
        Customer customer = new Customer();
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setUsername(customerDto.getUsername());
        customer.setPassword(customerDto.getPassword());
        customer.setCity(customerDto.getCity());
        customer.setPhone(customerDto.getPhone());
        customer.setEmail(customerDto.getEmail());
//        customer.setBirthDay(customerDto.getBirthDay());
        if (customerDto.getSex().equals("Male")) customer.setSex(1);
        else customer.setSex(0);
        customer.setRoles(Arrays.asList(roleRepository.findByName("CUSTOMER")));
        customer.setProvider(Provider.local.name());
        customer.setResetPasswordToken(customerDto.getResetPasswordToken());
        customer.setActive(customerDto.isActive());
        return customer;
    }


}
