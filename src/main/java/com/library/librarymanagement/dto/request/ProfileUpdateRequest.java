package com.library.librarymanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProfileUpdateRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    // cho phép trống; nếu có thì 8-15 chữ số, cho phép bắt đầu bằng +
    @Pattern(regexp = "^$|^\\+?[0-9]{8,15}$", message = "Phone is invalid (8-15 digits)")
    private String phone;
}
