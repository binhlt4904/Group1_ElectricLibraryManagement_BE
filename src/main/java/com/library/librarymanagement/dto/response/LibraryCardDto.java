package com.library.librarymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryCardDto {
    private Long id;
    private String cardNumber;
    private Date issueDate;
    private Date expiryDate;
    private String status;
    private Long readerId;
    private String readerName;
    private String readerCode;
    private Boolean isExpired;
    private Integer daysUntilExpiry;
}
