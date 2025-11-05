package com.library.librarymanagement.util;

import com.library.librarymanagement.dto.request.AccountRequest;
import com.library.librarymanagement.dto.response.BookReportResponse;
import com.library.librarymanagement.dto.response.BorrowRecordResponse;
import com.library.librarymanagement.entity.Account;
import com.library.librarymanagement.entity.BookReport;
import com.library.librarymanagement.entity.BorrowRecord;

import java.math.BigDecimal;

public class Mapper {
    public static Account mapDTOToEntity(AccountRequest accountRequest) {
        return Account.builder()
                .email(accountRequest.getEmail())
                .password(accountRequest.getPassword())
                .username(accountRequest.getUsername())
                .phone(accountRequest.getPhone())
                .fullName(accountRequest.getFullName())
                .build();
    }

    public static BorrowRecordResponse mapEntityToDTO(BorrowRecord borrowRecord) {
        return BorrowRecordResponse.builder()
                .id(borrowRecord.getId())
                .bookTitle(borrowRecord.getBook().getTitle())
                .authorName(borrowRecord.getBook().getAuthor().getFullName())
                .borrowedDate(borrowRecord.getBorrowedDate())
                .allowedDate(borrowRecord.getAllowedDate())
                .readerName(borrowRecord.getLibraryCard().getReader().getAccount().getFullName())
                .status(borrowRecord.getStatus())
                .fine(
                        borrowRecord.getReturnRecord() != null
                                ? borrowRecord.getReturnRecord().getFineAmount()
                                : BigDecimal.ZERO
                )
                .returnedDate(borrowRecord.getReturnRecord() != null
                                    ? borrowRecord.getReturnRecord().getReturnedDate() : null
                )
                .build();
    }

    public static BookReportResponse mapEntityToDTO(BookReport bookReport) {
        return BookReportResponse.builder()
                .id(bookReport.getId())
                .bookTitle(bookReport.getBook().getTitle())
                .authorName(bookReport.getBook().getAuthor().getFullName())
                .reporterName(bookReport.getReporter().getFullName())
                .reporterEmail(bookReport.getReporter().getEmail())
                .staffName(
                        bookReport.getStaff() != null
                            ? bookReport.getStaff().getFullName()
                            : null
                )
                .reportType(bookReport.getReportType())
                .description(bookReport.getDescription())
                .status(bookReport.getStatus())
                .createdAt(bookReport.getCreatedAt())
                .build();
    }

}
