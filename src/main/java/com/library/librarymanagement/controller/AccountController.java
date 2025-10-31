package com.library.librarymanagement.controller;

import com.library.librarymanagement.dto.request.AccountRequest;
import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.service.account.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Log4j2
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<String> importReaders(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File empty!");
        }

        accountService.importReaders(file);
        return ResponseEntity.ok("Import thành công!");
    }

}
