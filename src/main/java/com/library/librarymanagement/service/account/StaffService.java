package com.library.librarymanagement.service.account;

import com.library.librarymanagement.dto.response.StaffDetailDto;

public interface StaffService {
    StaffDetailDto getDetailByAccountId(Long accountId);
}
