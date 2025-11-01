package com.library.librarymanagement.controller.admin;

import com.library.librarymanagement.dto.request.CreateStaffRequest;
import com.library.librarymanagement.dto.request.UpdateAccountRequest;
import com.library.librarymanagement.dto.response.AccountDto;
import com.library.librarymanagement.dto.response.CreateStaffResponse;
import com.library.librarymanagement.dto.response.ReaderDetailDto;
import com.library.librarymanagement.dto.response.StaffDetailDto;
import com.library.librarymanagement.service.account.AccountService;
import com.library.librarymanagement.service.account.ReaderService;
import com.library.librarymanagement.service.account.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class ManageAccountController {

    private final AccountService accountService;
    private final ReaderService readerService;
    private final StaffService staffService;
    // lấy tất cả danh sách account lọc theo status, fullname, role
    @GetMapping("accounts")
    @PreAuthorize("hasRole('ADMIN')")
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
    // tạo mới 1 staff
    @PostMapping("accounts/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CreateStaffResponse> createStaff(
            @Valid @RequestBody CreateStaffRequest req
    ) {
        CreateStaffResponse res = accountService.createStaff(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
    //update cả hai loại account
    @PutMapping("accounts/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountDto> updateAccount(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateAccountRequest req
    ) {
        AccountDto res = accountService.updateAccount(id, req);
        return ResponseEntity.ok(res);
    }
    // delete cả hai loai account
    @DeleteMapping("accounts/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAccount(@PathVariable("id") Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build(); // 204
    }

    // view detail cho reader
    // ManageAccountController.java
    @GetMapping("accounts/reader/{accountId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReaderDetailDto> getReaderDetailByAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(readerService.getDetailByAccountId(accountId));
    }

    // View detail cho STAFF theo accountId
    @GetMapping("accounts/staff/{accountId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StaffDetailDto> getStaffDetail(@PathVariable Long accountId) {
        return ResponseEntity.ok(staffService.getDetailByAccountId(accountId));
    }

}
