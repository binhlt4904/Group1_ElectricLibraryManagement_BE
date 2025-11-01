package com.library.librarymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BorrowRecordResponse {
    private Long id;
    private String bookTitle;
    private String readerName;
    private String status;
    private Date borrowedDate;
}
