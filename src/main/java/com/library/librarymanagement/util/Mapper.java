package com.library.librarymanagement.util;

import com.library.librarymanagement.dto.request.AccountRequest;
import com.library.librarymanagement.dto.response.BorrowRecordResponse;
import com.library.librarymanagement.entity.Account;
import com.library.librarymanagement.entity.BorrowRecord;

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
                .borrowedDate(borrowRecord.getBorrowedDate())
                .readerName(borrowRecord.getLibraryCard().getReader().getAccount().getFullName())
                .status(borrowRecord.getStatus())
                .build();
    }

}
