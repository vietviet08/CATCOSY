package com.dacs1.library.service;

import com.dacs1.library.dto.CustomerDto;
import com.dacs1.library.model.Customer;

public interface CustomerService {
    Customer findByUsername(String username);

    Customer save(CustomerDto customerDto);
}
