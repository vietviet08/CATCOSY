package com.catcosy.library.service.impl;

import com.catcosy.library.repository.AdminRepository;
import com.catcosy.library.repository.RoleRepository;
import com.catcosy.library.dto.AdminDto;
import com.catcosy.library.model.Admin;
import com.catcosy.library.model.Role;
import com.catcosy.library.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private RoleRepository roleRepository;


    @Override
    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    @Override
    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    @Override
    public Admin findByEmail(String email) {
        return adminRepository.findByEmail(email);
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
//        admin.setRoles(Arrays.asList(roleRepository.findByName("ADMIN")));

        Role roleAdmin = roleRepository.findByName("ADMIN");
        if (roleAdmin == null) {
            throw new RuntimeException("Role ADMIN not found");
        }
        admin.setRoles(Arrays.asList(roleAdmin));

        return adminRepository.save(admin);
    }

    @Override
    public Admin saveEmployee(Admin admin) {
        return adminRepository.save(admin);
    }

    @Override
    public Admin update(AdminDto adminDto, Long id, MultipartFile image) {
        Admin admin = adminRepository.getReferenceById(id);
        try {
            admin.setFirstName(adminDto.getFirstName());
            admin.setLastName(adminDto.getLastName());
            admin.setPhone(adminDto.getPhone());
            admin.setEmail(adminDto.getEmail());
            if(!image.isEmpty())
                admin.setImage(Base64.getEncoder().encodeToString(image.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return adminRepository.save(admin);
    }

    @Transactional
    @Override
    public Admin updateEmployee(AdminDto admin, Long id, String roleUpdate) {
        Admin ad = adminRepository.getReferenceById(id);
        System.out.println(id);
        System.out.println(admin.getEmail());
        ad.setFirstName(admin.getFirstName());
        ad.setLastName(admin.getLastName());
        ad.setPhone(admin.getPhone());
        ad.setEmail(admin.getEmail());
        ad.getRoles().clear();
        ad.getRoles().add(roleRepository.findByName(roleUpdate));
        return adminRepository.save(ad);
    }

    @Override
    public Admin saveChangePassword(Long id, String currentPassword, String newPassword) {
        Admin admin = adminRepository.getReferenceById(id);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(currentPassword, admin.getPassword())) return null;
        admin.setPassword(passwordEncoder.encode(newPassword));
        return adminRepository.save(admin);
    }

    @Override
    public void lockEmployee(Long id, Principal principal) {
        Admin admin = adminRepository.getReferenceById(id);
        if (admin.getUsername().equals(principal.getName())) return;
        admin.setEnable(false);
        adminRepository.save(admin);
    }

    @Override
    public void activateEmployee(Long id) {
        Admin admin = adminRepository.getReferenceById(id);
        admin.setEnable(true);
        adminRepository.save(admin);
    }

    @Transactional
    @Override
    public void deleteEmployee(Long id, Principal principal) {
        Admin admin = adminRepository.getReferenceById(id);
        if (admin.getUsername().equals(principal.getName())) return;
        adminRepository.delete(admin);
    }
}
