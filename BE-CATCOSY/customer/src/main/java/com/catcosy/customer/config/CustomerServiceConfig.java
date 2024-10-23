package com.catcosy.customer.config;

import com.catcosy.library.model.Customer;
import com.catcosy.library.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomerServiceConfig implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByUsernameAndActive(username);
        if(customer == null) throw new UsernameNotFoundException("Not found user!");
        else if(!customer.isActive() ) throw  new UsernameNotFoundException("User is locked, please contact with admin!");

        return new User(customer.getUsername(),
                customer.getPassword(),
                customer.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList()));
    }
}
