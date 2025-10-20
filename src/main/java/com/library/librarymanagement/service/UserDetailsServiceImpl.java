package com.library.librarymanagement.service;

import com.library.librarymanagement.entity.Account;
import com.library.librarymanagement.repository.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsManager {
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> result = accountRepository.findByUsername(username);
        Account account = result.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(account.getRole().getName());

        UserDetails userResponse = User.builder()
                .username(account.getUsername())
                .password(account.getPassword())
                .authorities(grantedAuthority)
                .build();
        return userResponse;
    }
}
