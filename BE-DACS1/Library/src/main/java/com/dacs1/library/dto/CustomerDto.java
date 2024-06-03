package com.dacs1.library.dto;

import com.dacs1.library.model.City;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {
    private Long id;

    @Size(min = 3, max = 15, message = "First name from 3 to 15 characters")
    private String firstName;

    @Size(min = 3, max = 15, message = "Last name from 3 to 15 characters")
    private String lastName;

    @Size(min = 3, max = 15, message = "Username from 3 to 15 characters")
    private String username;

    @Size(min = 3, max = 15, message = "Username from 3 to 15 characters")
    private String password;

    private String passwordRepeat;

    private String phone;

    private String email;

    private City city;

    private String birthDay;

    private String sex;

    private String addressDetail;

    private String provider;

    private String resetPasswordToken;

    private boolean isActive = true;

}
