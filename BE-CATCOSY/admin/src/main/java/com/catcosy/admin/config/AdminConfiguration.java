package com.catcosy.admin.config;


import com.catcosy.admin.exeption.CustomAccessDeniedHandler;
import com.catcosy.admin.jwt.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class AdminConfiguration {

    @Bean
    public UserDetailsService userDetailsService() {
        return new AdminDetailsServiceConfig();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());


        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        // Enable CSRF protection for form submissions
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/do-login"))
                .authorizeHttpRequests(author ->
                        author .requestMatchers("/css/**", "/js/**", "/img/**", "/fonts/**").permitAll()
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                .requestMatchers("/forgot-password", "/register", "/register-new", "/do-login", "/login" , "/logout" ,"/", "/index" ).permitAll()
                                .requestMatchers("/categories/**").hasAnyAuthority("ADMIN","SELLER")
                                .requestMatchers("/brands/**").hasAnyAuthority("ADMIN","SELLER")
                                .requestMatchers("/products/**").hasAnyAuthority("ADMIN","SELLER")
                                .requestMatchers("/save-product").hasAnyAuthority("ADMIN","SELLER")
                                .requestMatchers("/orders/**").hasAnyAuthority("ADMIN","KEEPER")
                                .requestMatchers("/vouchers/**").hasAnyAuthority("ADMIN","SELLER")
                                .requestMatchers("/customers/**").hasAnyAuthority("ADMIN","KEEPER")
                                .requestMatchers("/employees/**", "/product/**").hasAuthority("ADMIN")
                                .anyRequest().authenticated()
                )

                .logout(logout ->
                        logout.invalidateHttpSession(true)
                                .clearAuthentication(true)
                                .deleteCookies("token")
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .logoutSuccessUrl("/login?logout")
                                .permitAll()
                )

                .authenticationManager(authenticationManager)
                // Change to ALWAYS to support form submissions
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authenticationProvider(daoAuthenticationProvider())
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex ->
                        ex.accessDeniedHandler(accessDeniedHandler)
                );
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}