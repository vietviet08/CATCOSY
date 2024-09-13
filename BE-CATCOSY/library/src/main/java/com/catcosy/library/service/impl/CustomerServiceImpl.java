package com.catcosy.library.service.impl;

import com.catcosy.library.repository.CustomerRepository;
import com.catcosy.library.repository.OrderRepository;
import com.catcosy.library.repository.RoleRepository;
import com.catcosy.library.dto.CustomerDto;
import com.catcosy.library.enums.Provider;
import com.catcosy.library.model.Admin;
import com.catcosy.library.model.Customer;
import com.catcosy.library.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private OrderRepository orderRepository;


    @Override
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    @Override
    public List<CustomerDto> getAllCustomer() {
        List<Customer> customers = customerRepository.findAll();

        List<CustomerDto> customerDtoList = new ArrayList<>();
        for(Customer customer : customers){
            customerDtoList.add(toDto(customer));
        }

        return customerDtoList;
    }

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
    public CustomerDto findByEmailAndProvider(String email, String provider) {
        return toDto(customerRepository.findByEmailAndProvider(email, provider));
    }

    @Override
    public CustomerDto findByEmail(String email) {
        return toDto(customerRepository.findByEmail(email));
    }


    @Override
    public Customer save(CustomerDto customerDto) {
        return customerRepository.save(toEntity(customerDto));
    }

    @Override
    public Customer saveByAdmin(Admin admin) {
        if(customerRepository.findByUsername(admin.getUsername()) != null || customerRepository.findByEmail(admin.getEmail()) != null) return null;

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        CustomerDto customerDto = new CustomerDto();
        customerDto.setUsername(admin.getUsername());
        customerDto.setFirstName(admin.getFirstName());
        customerDto.setLastName(admin.getLastName());
        customerDto.setEmail(admin.getEmail());
        customerDto.setPhone(admin.getPhone());
        customerDto.setPassword(passwordEncoder.encode(admin.getPassword()));
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

        if (customer == null) return null;

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

    @Override
    public Customer lockCustomer(String username) {
        Customer customer = customerRepository.findByUsername(username);
        customer.setActive(false);
        return customerRepository.save(customer);
    }

    @Override
    public Customer unlockCustomer(String username) {
        Customer customer = customerRepository.findByUsername(username);
        customer.setActive(true);
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public Customer deleteCustomer(String username) {
        Customer customer = customerRepository.findByUsername(username);
        orderRepository.deleteAllByIDCustomer(customer.getId());
        customerRepository.delete(customer);
        return customerRepository.findByUsername(username);
    }

    private CustomerDto toDto(Customer customer) {
        if (customer == null) return null;

        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(customer.getId());
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setLastName(customer.getLastName());
        customerDto.setUsername(customer.getUsername());
        customerDto.setPassword(customer.getPassword());
        customerDto.setCity(customer.getCity());
        customerDto.setPhone(customer.getPhone());
        customerDto.setEmail(customer.getEmail());

        if(customer.getBirthDay() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            customerDto.setBirthDay(sdf.format(customer.getBirthDay()));
        }

        if (customer.getSex() != null) if (customer.getSex() == 0) customerDto.setSex("Female");
        else customerDto.setSex("Male");

        customerDto.setAddressDetail(customer.getAddressDetail());
        customerDto.setProvider(customer.getProvider());
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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(customerDto.getBirthDay());
            java.sql.Date birthDay = new java.sql.Date(date.getTime());
            customer.setBirthDay(birthDay);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        if (customerDto.getSex().equals("Male")) customer.setSex(1);
        else customer.setSex(0);
        customer.setRoles(Arrays.asList(roleRepository.findByName("CUSTOMER")));
        customer.setProvider(Provider.local.name());
        customer.setResetPasswordToken(customerDto.getResetPasswordToken());
        customer.setActive(customerDto.isActive());
        return customer;
    }


}
