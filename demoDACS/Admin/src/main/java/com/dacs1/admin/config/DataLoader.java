package com.dacs1.admin.config;

import com.dacs1.library.model.Admin;
import com.dacs1.library.model.Role;
import com.dacs1.library.repository.AdminRepository;
import com.dacs1.library.repository.RoleRepository;
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
        createRoleIfNotFound(com.dacs1.library.enums.Role.ADMIN.name());
        createRoleIfNotFound(com.dacs1.library.enums.Role.SELLER.name());
        createRoleIfNotFound(com.dacs1.library.enums.Role.KEEPER.name());

        // Create admin account if it doesn't exist
        Admin admin = adminRepository.findByUsername(usernameAdmin);
        if (admin == null) {
            admin = new Admin();
            admin.setUsername(usernameAdmin);
            admin.setPassword(passwordEncoder.encode(passwordAdmin));
            Role adminRole = roleRepository.findByName(com.dacs1.library.enums.Role.ADMIN.name());
            admin.setRoles(Arrays.asList(adminRole));
            admin.setFirstName("new");
            admin.setLastName("admin");
            adminRepository.save(admin);
        }
    }

    private void createRoleIfNotFound(String roleName) {
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }
}
