package com.library.librarymanagement.service.account;

import com.library.librarymanagement.dto.request.AccountRequest;
import com.library.librarymanagement.dto.request.CreateStaffRequest;
import com.library.librarymanagement.dto.request.UpdateAccountRequest;
import com.library.librarymanagement.dto.response.AccountDto;
import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.dto.response.CreateStaffResponse;
import com.library.librarymanagement.entity.Account;
import org.springframework.data.domain.Page;

public interface AccountService {
    ApiResponse createAccount(AccountRequest accountRequest);
    //lấy về toàn bộ account lọc theo fullname, status và role
    Page<AccountDto> findAll(String fullName, String status, Long roleId, int page, int size);
    CreateStaffResponse createStaff(CreateStaffRequest req);
    AccountDto updateAccount(Long accountId, UpdateAccountRequest req);
}
