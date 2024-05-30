package com.dacs1.library.service;

import com.dacs1.library.dto.AdminDto;
import com.dacs1.library.model.Admin;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;


public interface AdminService {

    List<Admin> findAll();

    Admin findByUsername(String username);

    Admin findByEmail(String email);

    UserDetails finByUsername(String username);

    Admin save(AdminDto adminDto);

    Admin saveEmployee(Admin admin);

    Admin update(AdminDto adminDto, Long id, MultipartFile image);

    Admin updateEmployee(AdminDto admin, Long id, String roleUpdate);

    Admin saveChangePassword(Long id, String currentPassword, String newPassword);

    void lockEmployee(Long id, Principal principal);

    void activateEmployee(Long id);

    void deleteEmployee(Long id, Principal principal);

}
