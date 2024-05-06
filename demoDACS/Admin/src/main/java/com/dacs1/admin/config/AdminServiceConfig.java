//package com.dacs1.admin.config;
//
//import com.dacs1.library.model.AdminDetails;
//import com.dacs1.library.repository.AdminDetailsRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.stream.Collectors;
//
//@Service
//public class AdminServiceConfig implements UserDetailsService {
//
//    @Autowired
//    private  AdminDetailsRepository adminDetailsRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        AdminDetails admin = adminDetailsRepository.findByUsername(username).orElseThrow();
//        if (admin == null)
//            throw new UsernameNotFoundException("Not found username");
//
//        return new User(admin.getUsername(),
//                admin.getPassword(),
//                admin.getRole()
//                        .stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList()));
//    }
//}
