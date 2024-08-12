package com.scm.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserForm {
    
    @NotBlank(message = "Name is required")
    @Size(min = 3, message = "Min 3 characters are required")
    @NotBlank(message = "Name is required")
    @Size(min = 3, message = "Min 3 characters are required")
    private String name;

    @Email(message = "Invalid email address")
    @NotBlank(message = "Email is required")

    @Email(message = "Invalid email address")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Min 6 characters are required")

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Min 6 characters are required")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Size(min = 8, max = 10, message = "Min 8 and Max 10 digits are required")

    @NotBlank(message = "Phone number is required")
    @Size(min = 8, max = 10, message = "Min 8 and Max 10 digits are required")
    private String phoneNumber;

    @NotBlank(message = "About is required")

    @NotBlank(message = "About is required")
    private String about;
}

// spring-boot-starter-validation -> dependency used for validation

// spring-boot-starter-validation -> dependency used for validation
