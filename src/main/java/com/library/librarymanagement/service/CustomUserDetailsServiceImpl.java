package com.library.librarymanagement.service;

import com.library.librarymanagement.entity.Account;
import com.library.librarymanagement.repository.ReaderRepository;
import com.library.librarymanagement.repository.account.AccountRepository;
import com.library.librarymanagement.service.custom_user_details.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements UserDetailsManager {
    private final ReaderRepository readerRepository;
    private final AccountRepository accountRepository;
    @Override
    public void createUser(UserDetails user) {

    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // ✅ luôn chắc chắn lấy readerId chính xác dù lazy
        Long readerId = readerRepository.findByAccountId(account.getId())
                .map(r -> {
                    System.out.println("✅ Reader found with id = " + r.getId());
                    return r.getId();
                })
                .orElse(null);

        if (readerId == null) {
            System.out.println("⚠️ No reader linked to this account!");
        }

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(account.getRole().getName());
        return new CustomUserDetails(readerId, account.getId(), account.getUsername(), account.getPassword(), List.of(authority));
    }
}
