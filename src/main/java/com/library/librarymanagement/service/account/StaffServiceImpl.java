package com.library.librarymanagement.service.account;// src/main/java/com/library/librarymanagement/service/account/impl/StaffServiceImpl.java


import com.library.librarymanagement.dto.response.StaffDetailDto;
import com.library.librarymanagement.entity.Account;
import com.library.librarymanagement.entity.SystemUser;
import com.library.librarymanagement.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final SystemUserRepository systemUserRepository;

    private static String formatDate(java.sql.Timestamp t) {
        return t == null ? null : t.toInstant().atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    @Override
    @Transactional(readOnly = true)
    public StaffDetailDto getDetailByAccountId(Long accountId) {
        SystemUser su = systemUserRepository.fetchByAccountId(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Staff (SystemUser) not found for accountId=" + accountId));

        Account a = su.getAccount();
        return StaffDetailDto.builder()
                .username(a.getUsername())
                .email(a.getEmail())
                .fullName(a.getFullName())
                .phone(a.getPhone())
                .status(a.getStatus())
                .position(su.getPosition())
                .joinDate(formatDate(su.getJoinDate()))
                .hireDate(formatDate(su.getHireDate()))
                .build();
    }
}
