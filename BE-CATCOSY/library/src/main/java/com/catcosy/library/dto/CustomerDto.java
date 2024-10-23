package com.catcosy.library.dto;

import com.catcosy.library.model.City;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
