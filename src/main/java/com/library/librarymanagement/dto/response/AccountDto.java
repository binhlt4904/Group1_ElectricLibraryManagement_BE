package com.library.librarymanagement.dto.response;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDto {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String status;
    private String role;
}
