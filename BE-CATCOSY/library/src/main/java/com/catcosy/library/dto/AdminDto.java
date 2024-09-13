package com.catcosy.library.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDto {
    @Size(min = 3, max = 15, message = "Invalid first name! (3 - 15 characters)")
    private String firstName;
    @Size(min = 3, max = 15, message = "Invalid last name! (3 - 15 characters)")
    private String lastName;
    private String username;
    @Size(min = 3, max = 15, message = "Invalid password name! (3 - 15 characters)")
    private String password;
    private String repeatPassword;
    private String email;
    private String phone;

}