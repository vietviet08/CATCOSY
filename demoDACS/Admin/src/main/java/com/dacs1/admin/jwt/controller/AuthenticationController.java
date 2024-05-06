package com.dacs1.admin.jwt.controller;

import com.dacs1.library.model.AuthenticationRequest;
import com.dacs1.admin.jwt.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("do-login")
    public String doLogin(@RequestBody() AuthenticationRequest request) {
        try {
            authenticationService.authenticate(request);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "index";
    }

}
