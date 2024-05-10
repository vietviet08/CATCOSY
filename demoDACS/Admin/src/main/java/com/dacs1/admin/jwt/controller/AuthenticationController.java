package com.dacs1.admin.jwt.controller;

import com.dacs1.admin.config.AdminDetailsServiceConfig;
import com.dacs1.admin.jwt.JwtUtils;
import com.dacs1.library.model.AuthenticationRequest;
import com.dacs1.library.model.AuthenticationResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AdminDetailsServiceConfig adminDetailsServiceConfig;


    @PostMapping("/do-login")
    public String doLogin(@ModelAttribute("authRequest") AuthenticationRequest request,
                          HttpServletResponse response,
                          RedirectAttributes attributes) {
        try {


            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            var user = adminDetailsServiceConfig.loadUserByUsername(request.getUsername());
            var jwtToken = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.refreshToken(user);
//        revokeAllUserTokens(user);
//        saveUserToken(user, jwtToken);
            AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();

            Cookie cookie = new Cookie("token", jwtToken);

//            cookie.setMaxAge(3600 * 24);
            response.addCookie(cookie);

        }catch (DisabledException e){
            attributes.addFlashAttribute("warning", "Your account has been locked, please contact to ADMIN!");
            return "redirect:/login";
        }
        catch (BadCredentialsException e) {
            attributes.addFlashAttribute("error", "Username or password not correct!");
            return "redirect:/login";
        }catch (Exception e){
            attributes.addFlashAttribute("error", "Error from server!");
            return "redirect:/login";
        }

        attributes.addFlashAttribute("success", "Welcome back, have a nice day!");
        return "redirect:/index";
    }


}
