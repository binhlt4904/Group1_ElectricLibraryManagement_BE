package com.library.librarymanagement.controller;

import com.library.librarymanagement.dto.request.AccountRequest;
import com.library.librarymanagement.dto.request.ForgetPasswordRequest;
import com.library.librarymanagement.dto.request.ResetPasswordRequest;
import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.service.account.AccountService;
import com.library.librarymanagement.service.account.RateLimiterService;
import jakarta.persistence.PostRemove;
import jakarta.servlet.http.HttpServletRequest;
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
    private final RateLimiterService rateLimiterService;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<String> importReaders(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File empty!");
        }

        accountService.importReaders(file);
        return ResponseEntity.ok("Import thành công!");
    }

    @PostMapping("/forget-password")
    public ResponseEntity<String> forgetPassword(@RequestBody ForgetPasswordRequest request, HttpServletRequest httpRequest) {
        if (request.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Email empty!");
        }
        String ip = httpRequest.getRemoteAddr();
        if (!rateLimiterService.tryConsume(ip)) {
            return ResponseEntity.status(404).body("Too many requests. Please try again later.");
        }

        accountService.forgetPassword(request.getEmail());
        return ResponseEntity.ok("Link that reset password, is sent to your email!");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request,
                                                @RequestParam String token) {
        if (request.getNewPassword().isBlank()) {
            return ResponseEntity.badRequest().body("New password is empty!");
        }
        accountService.resetPassword(token, request.getNewPassword());
        return ResponseEntity.ok("System updated password successfully!");
    }

}
