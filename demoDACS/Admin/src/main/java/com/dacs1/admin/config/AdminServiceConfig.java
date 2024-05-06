package com.dacs1.admin.config;

import com.dacs1.library.model.Admin;
//import com.dacs1.library.repository.AdminDetailsRepository;
import com.dacs1.library.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AdminServiceConfig implements UserDetailsService {

    @Autowired
    private AdminRepository adminDetailsRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminDetailsRepository.findByUsername(username);
        if (admin == null)
            throw new UsernameNotFoundException("Not found username");

        return new User(admin.getUsername(),
                admin.getPassword(),
                admin.getRoles()
                        .stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList()));
    }
}
