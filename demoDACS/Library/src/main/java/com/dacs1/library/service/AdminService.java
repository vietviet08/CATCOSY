package com.dacs1.library.service;

import com.dacs1.library.dto.AdminDto;
import com.dacs1.library.model.Admin;


public interface AdminService {

    Admin findByUsername(String username);

    Admin save(AdminDto adminDto);




}
