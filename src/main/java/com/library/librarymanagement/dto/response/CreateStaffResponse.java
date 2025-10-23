package com.library.librarymanagement.dto.response;// src/main/java/com/library/librarymanagement/dto/staff/StaffResponse.java


import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateStaffResponse {
    private Long accountId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String status;     // ACTIVE/INACTIVE
    private String role;       // ROLE_STAFF

    private Long systemUserId;
    private String position;
    private BigDecimal salary;
    private LocalDateTime joinDate;
    private LocalDateTime hireDate;

    private LocalDateTime createdDate;
}
