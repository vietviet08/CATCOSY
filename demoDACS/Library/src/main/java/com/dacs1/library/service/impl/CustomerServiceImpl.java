package com.dacs1.library.service.impl;

import com.dacs1.library.dto.CustomerDto;
import com.dacs1.library.model.Customer;
import com.dacs1.library.repository.CustomerRepository;
import com.dacs1.library.repository.RoleRepository;
import com.dacs1.library.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

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
    public Customer save(CustomerDto customerDto) {
        return customerRepository.save(toEntity(customerDto));
    }

    @Override
    public Customer updateCustomer(Customer customer) {

        return customerRepository.save(customer);
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
        if (customer.getSex() == 0) customerDto.setSex("Female");
        else customerDto.setSex("Male");

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

        return customer;
    }


}