package com.library.librarymanagement.dto.response;// src/main/java/edu/lms/dto/reader/ReaderDetailDTO.java
import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ReaderDetailDto {
    private String email;
    private String fullName;
    private String username;
    private String phone;
    private String status;     // ACTIVE | INACTIVE | DELETED
    private String readerCode;
}
