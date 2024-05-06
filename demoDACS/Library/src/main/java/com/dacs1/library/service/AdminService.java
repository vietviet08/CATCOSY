package com.dacs1.library.service;

import com.dacs1.library.dto.AdminDto;
import com.dacs1.library.model.Admin;
import org.springframework.security.core.userdetails.UserDetails;


public interface AdminService {

    Admin findByUsername(String username);

    UserDetails finByUsername(String username);

    Admin save(AdminDto adminDto);


}
