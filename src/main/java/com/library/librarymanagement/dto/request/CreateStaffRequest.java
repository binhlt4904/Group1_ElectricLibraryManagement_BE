package com.library.librarymanagement.dto.request;// src/main/java/com/library/librarymanagement/dto/staff/StaffCreateRequest.java


import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateStaffRequest {
    // account info
    @NotBlank @Size(min = 3, max = 50)
    private String username;

    @NotBlank @Email
    private String email;

    @NotBlank
    private String fullName;

    @Size(max = 20)
    private String phone;

    @NotBlank @Size(min = 6, max = 100)
    private String password;

    // ACTIVE/INACTIVE (optional, default ACTIVE)
    private String status;

    // system_user info
    @NotBlank
    private String position;

    @NotNull @DecimalMin(value = "0.00")
    private BigDecimal salary;

    // Optional; nếu null sẽ auto = now
    private LocalDateTime joinDate;
    private LocalDateTime hireDate;
}
