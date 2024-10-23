package com.catcosy.admin.jwt.service;


import com.catcosy.library.model.AuthenticationRequest;
import com.catcosy.library.model.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse authenticate (AuthenticationRequest request);
}
