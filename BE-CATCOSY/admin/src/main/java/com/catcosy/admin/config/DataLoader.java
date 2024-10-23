package com.catcosy.admin.config;

import com.catcosy.library.enums.Role;
import com.catcosy.library.model.Admin;
import com.catcosy.library.repository.AdminRepository;
import com.catcosy.library.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Component
@Transactional
public class DataLoader implements CommandLineRunner {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String usernameAdmin;

    @Value("${admin.password}")
    private String passwordAdmin;

    @Override
    public void run(String... args) throws Exception {

        // Save roles to the database if they don't exist
        createRoleIfNotFound(Role.ADMIN.name());
        createRoleIfNotFound(Role.SELLER.name());
        createRoleIfNotFound(Role.KEEPER.name());

        // Create admin account if it doesn't exist
        Admin admin = adminRepository.findByUsername(usernameAdmin);
        if (admin == null) {
            admin = new Admin();
            admin.setUsername(usernameAdmin);
            admin.setPassword(passwordEncoder.encode(passwordAdmin));
            com.catcosy.library.model.Role adminRole = roleRepository.findByName(Role.ADMIN.name());
            admin.setRoles(Arrays.asList(adminRole));
            admin.setFirstName("new");
            admin.setLastName("admin");
            adminRepository.save(admin);
        }
    }

    private void createRoleIfNotFound(String roleName) {
        com.catcosy.library.model.Role role = roleRepository.findByName(roleName);
        if (role == null) {
            role = new com.catcosy.library.model.Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }
}
