package com.library.librarymanagement.service.account;

import com.library.librarymanagement.dto.response.ReaderDetailDto;

public interface ReaderService {
    ReaderDetailDto getDetailByAccountId(Long accountId);
}
