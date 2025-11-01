// src/main/java/com/library/librarymanagement/dto/response/StaffDetailDto.java
package com.library.librarymanagement.dto.response;

import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class StaffDetailDto {

    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String status;   // ACTIVE | INACTIVE | DELETED

    private String position;
    private String joinDate;
    private String hireDate;
}
