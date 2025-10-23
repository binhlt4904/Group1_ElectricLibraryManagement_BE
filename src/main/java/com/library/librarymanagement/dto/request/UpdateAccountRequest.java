// src/main/java/com/library/librarymanagement/dto/account/AccountUpdateRequest.java
package com.library.librarymanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAccountRequest {

    // Tất cả field đều optional; nếu null/blank => bỏ qua, không cập nhật

    @Size(min = 3, max = 50)
    private String username;

    // FE đã confirm password; BE chỉ cần mã hoá nếu có giá trị
    @Size(min = 6, max = 100)
    private String password;

    private String status;     // ACTIVE / INACTIVE

    private String fullName;

    @Email
    private String email;

    @Size(max = 20)
    private String phone;
}
