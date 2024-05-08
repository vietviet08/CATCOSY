package com.dacs1.library.service.impl;

import com.dacs1.library.dto.AdminDto;
import com.dacs1.library.model.Admin;
import com.dacs1.library.repository.AdminRepository;
import com.dacs1.library.repository.RoleRepository;
import com.dacs1.library.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private RoleRepository roleRepository;


    @Override
    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    @Override
    public UserDetails finByUsername(String username) throws UsernameNotFoundException {
        return (UserDetails) adminRepository.findByUsername(username);
    }

    @Override
    public Admin save(AdminDto adminDto) {
        Admin admin = new Admin();

        admin.setFirstName(adminDto.getFirstName());
        admin.setLastName(adminDto.getLastName());
        admin.setUsername(adminDto.getUsername());
        admin.setPassword(adminDto.getPassword());
        admin.setEmail(adminDto.getEmail());
        admin.setPhone(adminDto.getPhone());
        admin.setRoles(Arrays.asList(roleRepository.findByName("ADMIN")));

        return adminRepository.save(admin);
    }

    @Override
    public Admin update(AdminDto adminDto, Long id) {
        Admin admin = adminRepository.getReferenceById(id);
        admin.setFirstName(adminDto.getFirstName());
        admin.setLastName(adminDto.getLastName());
        admin.setPhone(adminDto.getPhone());
        admin.setEmail(adminDto.getEmail());
        return adminRepository.save(admin);
    }

    @Override
    public Admin saveChangePassword(Long id, String currentPassword, String newPassword) {
        Admin admin = adminRepository.getReferenceById(id);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(currentPassword, admin.getPassword())) return null;
        admin.setPassword(passwordEncoder.encode(newPassword));
        return adminRepository.save(admin);
    }
}
