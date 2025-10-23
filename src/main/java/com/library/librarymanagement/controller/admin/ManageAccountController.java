package com.library.librarymanagement.controller.admin;

import com.library.librarymanagement.dto.request.CreateStaffRequest;
import com.library.librarymanagement.dto.request.UpdateAccountRequest;
import com.library.librarymanagement.dto.response.AccountDto;
import com.library.librarymanagement.dto.response.CreateStaffResponse;
import com.library.librarymanagement.service.account.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class ManageAccountController {

    private final AccountService accountService;
    // lấy tất cả danh sách account lọc theo status, fullname, role
    @GetMapping("accounts")
    public ResponseEntity<Page<AccountDto>> list(
            @RequestParam(name = "full_name", required = false) String fullName,
            @RequestParam(required = false) String status,
            @RequestParam(name = "role_id", required = false) Long roleId, // lọc theo ID
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<AccountDto> result = accountService.findAll(fullName, status, roleId, page, size);
        return ResponseEntity.ok(result);
    }
    @PostMapping("accounts/create")
    public ResponseEntity<CreateStaffResponse> createStaff(
            @Valid @RequestBody CreateStaffRequest req
    ) {
        CreateStaffResponse res = accountService.createStaff(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
    @PutMapping("accounts/update/{id}")
    public ResponseEntity<AccountDto> updateAccount(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateAccountRequest req
    ) {
        AccountDto res = accountService.updateAccount(id, req);
        return ResponseEntity.ok(res);
    }
}
