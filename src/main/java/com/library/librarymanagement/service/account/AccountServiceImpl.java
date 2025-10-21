package com.library.librarymanagement.service.account;

import com.library.librarymanagement.dto.request.AccountRequest;
import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.entity.Account;
import com.library.librarymanagement.entity.Role;
import com.library.librarymanagement.exception.ConstraintViolationException;
import com.library.librarymanagement.exception.ExistAttributeValueException;
import com.library.librarymanagement.exception.ObjectNotExistException;
import com.library.librarymanagement.repository.account.AccountRepository;
import com.library.librarymanagement.repository.role.RoleRepository;
import com.library.librarymanagement.util.Mapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${default.role}")
    private String defaultRole;

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
}
