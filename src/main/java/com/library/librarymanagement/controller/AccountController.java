package com.library.librarymanagement.controller;

import com.library.librarymanagement.dto.request.AccountRequest;
import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.service.account.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Log4j2
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/")
    public ResponseEntity<ApiResponse> createAccount(@Valid @RequestBody AccountRequest accountRequest) {
        return ResponseEntity.ok(accountService.createAccount(accountRequest));
    }



}
