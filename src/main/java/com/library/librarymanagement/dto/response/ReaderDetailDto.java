package com.library.librarymanagement.dto.response;// src/main/java/edu/lms/dto/reader/ReaderDetailDTO.java
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ReaderDetailDto {
    private Long id;           // Reader ID - needed for library card creation
    private String email;
    private String fullName;
    private String username;
    private String phone;
    private String status;     // ACTIVE | INACTIVE | DELETED
    private String readerCode;
}
