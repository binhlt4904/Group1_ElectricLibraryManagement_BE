package com.library.librarymanagement.service.account;

import com.library.librarymanagement.dto.request.AccountRequest;
import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.entity.Account;

public interface AccountService {
    ApiResponse createAccount(AccountRequest accountRequest);

}
