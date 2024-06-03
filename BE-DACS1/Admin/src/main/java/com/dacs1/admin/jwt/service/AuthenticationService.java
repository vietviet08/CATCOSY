package com.dacs1.admin.jwt.service;


import com.dacs1.library.model.AuthenticationRequest;
import com.dacs1.library.model.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse authenticate (AuthenticationRequest request);
}
