package com.library.librarymanagement.service.account;// src/main/java/edu/lms/service/impl/ReaderServiceImpl.java

import com.library.librarymanagement.dto.response.ReaderDetailDto;
import com.library.librarymanagement.entity.Account;
import com.library.librarymanagement.entity.Reader;
import com.library.librarymanagement.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ReaderServiceImpl implements ReaderService {

    private final ReaderRepository readerRepository;

    // ReaderServiceImpl.java
    @Override
    @Transactional(readOnly = true)
    public ReaderDetailDto getDetailByAccountId(Long accountId) {
        Reader r = readerRepository.fetchDetailByAccountId(accountId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Reader not found for accountId=" + accountId));

        Account a = r.getAccount();
        return ReaderDetailDto.builder()
                .email(a.getEmail())
                .fullName(a.getFullName())
                .username(a.getUsername())
                .phone(a.getPhone())
                .status(a.getStatus())
                .readerCode(r.getReaderCode())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReaderDetailDto> searchReaders(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Reader> readers;
        
        if (query == null || query.trim().isEmpty()) {
            // If no query, return all readers
            readers = readerRepository.findAll(pageable);
        } else {
            // Search by fullName or readerCode
            readers = readerRepository.searchByFullNameOrReaderCode(query.trim(), pageable);
        }
        
        // Map to DTO
        return readers.map(r -> {
            Account a = r.getAccount();
            return ReaderDetailDto.builder()
                    .email(a.getEmail())
                    .fullName(a.getFullName())
                    .username(a.getUsername())
                    .phone(a.getPhone())
                    .status(a.getStatus())
                    .readerCode(r.getReaderCode())
                    .build();
        });
    }

}
