package com.library.librarymanagement.service.account;

import com.library.librarymanagement.dto.response.ReaderDetailDto;
import org.springframework.data.domain.Page;

public interface ReaderService {
    ReaderDetailDto getDetailByAccountId(Long accountId);
    
    /**
     * Search readers by fullName or readerCode
     * @param query Search query (fullName or readerCode)
     * @param page Page number
     * @param size Page size
     * @return Page of ReaderDetailDto
     */
    Page<ReaderDetailDto> searchReaders(String query, int page, int size);
}
