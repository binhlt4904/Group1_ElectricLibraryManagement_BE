package com.library.librarymanagement.service.account;

import com.library.librarymanagement.dto.request.AccountRequest;
import com.library.librarymanagement.dto.request.CreateStaffRequest;
import com.library.librarymanagement.dto.request.UpdateAccountRequest;
import com.library.librarymanagement.dto.response.AccountDto;
import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.dto.response.CreateStaffResponse;
import com.library.librarymanagement.entity.Account;
import com.library.librarymanagement.entity.Role;
import com.library.librarymanagement.entity.SystemUser;
import com.library.librarymanagement.exception.ConstraintViolationException;
import com.library.librarymanagement.exception.ExistAttributeValueException;
import com.library.librarymanagement.exception.ObjectNotExistException;
import com.library.librarymanagement.repository.SystemUserRepository;
import com.library.librarymanagement.repository.account.AccountRepository;
import com.library.librarymanagement.repository.role.RoleRepository;
import com.library.librarymanagement.util.Mapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SystemUserRepository systemUserRepository;
    @Value("${default.role}")
    private String defaultRole;
    private static final long ROLE_STAFF  = 2;
    private static final long ROLE_READER = 3;

    @Override
    @Transactional
    public ApiResponse createAccount(AccountRequest accountRequest) {

        Optional<Role> result = roleRepository.findByName(defaultRole);
        if(!result.isPresent()) {
            throw new ObjectNotExistException("Role Not Found");
        }
        Account account = Mapper.mapDTOToEntity(accountRequest);
        String encodedPassword = passwordEncoder.encode(accountRequest.getPassword());
        account.setPassword(encodedPassword);
        account.setRole(result.get());
        account.setStatus("Active");

        if (accountRepository.findByUsername(accountRequest.getUsername()).isPresent()) {
            throw new ExistAttributeValueException("Username already exists");
        }
        if(accountRepository.findByPhone(accountRequest.getPhone()).isPresent()) {
            throw new ExistAttributeValueException("Phone number already exists");
        }

        try {
            Account savedAccount = accountRepository.save(account);
            boolean success = savedAccount != null && savedAccount.getId() != null;

            return ApiResponse.builder()
                    .success(success)
                    .message("Account created successfully")
                    .build();
        } catch (Exception e){
            throw new ConstraintViolationException("Violate constraint in database");
        }
    }

    @Override
    public Page<AccountDto> findAll(String fullName, String status, Long roleId, int page, int size) {
        // chuẩn hoá input
        String fn = (fullName == null || fullName.isBlank()) ? null : fullName.trim();
        String st = (status   == null || status.isBlank())   ? null : status.trim();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<Account> listAccounts = accountRepository.getAllAccounts(fn, st, roleId, pageable);
        return listAccounts.map(a -> AccountDto.builder()
                .id(a.getId())
                .username(a.getUsername())
                .fullName(a.getFullName())
                .email(a.getEmail())
                .phone(a.getPhone())
                .status(a.getStatus())
                .role(a.getRole() != null ? a.getRole().getName() : null)
                .build());
    }

    @Override
    public CreateStaffResponse createStaff(CreateStaffRequest req) {
        // 1) Validate uniqueness
        if (accountRepository.existsByUsernameIgnoreCase(req.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (accountRepository.existsByEmailIgnoreCase(req.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // 2) Load STAFF role (id = 2)
        Role staffRole = roleRepository.findById(Long.valueOf("2"))
                .orElseThrow(() -> new IllegalStateException("ROLE_STAFF (id=2) not found"));

        // 3) Create Account
        Account acc = new Account();
        acc.setUsername(req.getUsername().trim());
        acc.setEmail(req.getEmail().trim());
        acc.setFullName(req.getFullName().trim());
        acc.setPhone(req.getPhone());
        acc.setStatus((req.getStatus() == null || req.getStatus().isBlank()) ? "ACTIVE" : req.getStatus().trim());
        acc.setPassword(passwordEncoder.encode(req.getPassword()));
        acc.setRole(staffRole);
        accountRepository.save(acc);

        // 4) Create SystemUser (timestamps)
        LocalDateTime now = LocalDateTime.now();
        Timestamp joinTs = Timestamp.valueOf(req.getJoinDate() != null ? req.getJoinDate() : now);
        Timestamp hireTs = Timestamp.valueOf(req.getHireDate() != null ? req.getHireDate() : joinTs.toLocalDateTime());

        SystemUser su = new SystemUser();
        su.setAccount(acc);
        su.setPosition(req.getPosition().trim());
        su.setSalary(req.getSalary());
        su.setJoinDate(joinTs);
        su.setHireDate(hireTs);

        su.setIsDeleted(false);
        su.setCreatedDate(Timestamp.valueOf(now));
        su.setUpdatedDate(null);
        su.setCreatedBy(Long.valueOf("1"));  // default là admin tạo
        su.setUpdatedBy(null);

        systemUserRepository.save(su);

        // 5) Build response
        return CreateStaffResponse.builder()
                .accountId(acc.getId())
                .username(acc.getUsername())
                .fullName(acc.getFullName())
                .email(acc.getEmail())
                .phone(acc.getPhone())
                .status(acc.getStatus())
                .role(staffRole.getName()) // ví dụ ROLE_STAFF

                .systemUserId(su.getId())
                .position(su.getPosition())
                .salary(su.getSalary())
                .joinDate(su.getJoinDate().toLocalDateTime())
                .hireDate(su.getHireDate().toLocalDateTime())
                .createdDate(su.getCreatedDate().toLocalDateTime())
                .build();
    }

    @Override
    @Transactional
    public AccountDto updateAccount(Long accountId, UpdateAccountRequest req) {
        Account acc = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        // Chỉ cho update Staff/Reader
        Long roleId = acc.getRole() != null ? acc.getRole().getId() : null;
        if (roleId == null || !(roleId == ROLE_STAFF || roleId == ROLE_READER)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only STAFF/READER can be updated via this API");
        }

        // username
        if (req.getUsername() != null && !req.getUsername().isBlank()) {
            String newUsername = req.getUsername().trim();
            if (accountRepository.existsByUsernameIgnoreCaseAndIdNot(newUsername, accountId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
            }
            acc.setUsername(newUsername);
        }

        // password (mã hoá nếu có)
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            acc.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        // status
        if (req.getStatus() != null && !req.getStatus().isBlank()) {
            acc.setStatus(req.getStatus().trim());
        }

        // fullName
        if (req.getFullName() != null && !req.getFullName().isBlank()) {
            acc.setFullName(req.getFullName().trim());
        }

        // email
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            String newEmail = req.getEmail().trim();
            if (accountRepository.existsByEmailIgnoreCaseAndIdNot(newEmail, accountId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
            }
            acc.setEmail(newEmail);
        }

        // phone
        if (req.getPhone() != null && !req.getPhone().isBlank()) {
            acc.setPhone(req.getPhone().trim());
        }

        accountRepository.save(acc);

        return AccountDto.builder()
                .id(acc.getId())
                .username(acc.getUsername())
                .fullName(acc.getFullName())
                .email(acc.getEmail())
                .phone(acc.getPhone())
                .status(acc.getStatus())
                .role(acc.getRole() != null ? acc.getRole().getName() : null)
                .build();
    }
}

