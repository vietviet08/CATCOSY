package com.dacs1.admin.jwt.service.impl;

import com.dacs1.admin.jwt.JwtUtils;
import com.dacs1.library.model.AuthenticationRequest;
import com.dacs1.library.model.AuthenticationResponse;
import com.dacs1.library.repository.AdminDetailsRepository;
import com.dacs1.admin.jwt.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private AdminDetailsRepository repository;

    private JwtUtils jwtUtils;

    private AuthenticationManager authenticationManager;

    public AuthenticationServiceImpl(AdminDetailsRepository repository, JwtUtils jwtUtils, AuthenticationManager authenticate) {
        this.repository = repository;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticate;
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = repository.findByUsername(request.getUsername()).orElseThrow();
        var jwtToken = jwtUtils.generateToken(user);
        var refreshToken = jwtUtils.generateRefreshToken(user);
//        revokeAllUserTokens(user);
//        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }
}
