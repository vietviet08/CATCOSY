package com.dacs1.customer.config;

import com.dacs1.customer.config.handler.CustomAuthenticationFailureHandler;
import com.dacs1.customer.config.oauth2.CustomOauth2Service;
import com.dacs1.customer.config.oauth2.CustomOauth2User;
import com.dacs1.customer.config.oauth2.OAuth2LoginSuccessHandler;
import com.dacs1.library.service.CustomerService;
import com.dacs1.library.service.oauth2.security.OAuth2DetailCustomService;
import com.dacs1.library.service.oauth2.security.handler.OAuth2FailureHandler;
import com.dacs1.library.service.oauth2.security.handler.OAuth2SuccessHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class CustomerConfiguration {


    /*old*/
    @Autowired
    private CustomOauth2Service customOauth2Service;

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    /*old*/

    /*new*/
    @Autowired
    private OAuth2DetailCustomService oAuth2DetailCustomService;

    @Autowired
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @Autowired
    private OAuth2FailureHandler oAuth2FailureHandler;
    /*new*/

    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomerServiceConfig();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(author ->
                        author.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                .requestMatchers("/detail-order/").permitAll()
                                .requestMatchers("/orders/view/**").permitAll()
                                .requestMatchers("/find-product/*").hasAuthority("CUSTOMER")
                                .requestMatchers("/*", "/product-detail/**").permitAll()
                                .anyRequest().authenticated()
                )

                .formLogin(login ->
                        login.loginPage("/login")
                                .loginProcessingUrl("/do-login")
                                .defaultSuccessUrl("/shop", true)
                                .failureHandler(customAuthenticationFailureHandler)
                                .permitAll()
                )
                .oauth2Login(oauth2 ->
                        oauth2.loginPage("/login")
                                .userInfoEndpoint(userInfoEndpointConfig ->
                                        userInfoEndpointConfig.userService(oAuth2DetailCustomService))
                                .successHandler(oAuth2SuccessHandler)
                                .failureHandler(oAuth2FailureHandler)
//                                .defaultSuccessUrl("/hello-world", true)
                )
                .rememberMe(rememberMe ->
                        rememberMe.key("mySecretKey")
                                .tokenValiditySeconds(3600 * 24 * 7)
                                .userDetailsService(userDetailsService())
                )

                .logout(logout ->
                        logout.invalidateHttpSession(true)
                                .clearAuthentication(true)
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .logoutSuccessUrl("/login?logout")
                                .permitAll()
                )
                .authenticationManager(authenticationManager)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                );

        return http.build();
    }


}
