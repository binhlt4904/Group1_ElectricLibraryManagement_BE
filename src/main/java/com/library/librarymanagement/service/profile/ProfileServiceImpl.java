package com.library.librarymanagement.service.profile;

import com.library.librarymanagement.dto.request.ChangePasswordRequest;
import com.library.librarymanagement.dto.request.ProfileUpdateRequest;
import com.library.librarymanagement.dto.response.ReaderDetailDto;
import com.library.librarymanagement.dto.response.StaffDetailDto;
import com.library.librarymanagement.entity.Account;
import com.library.librarymanagement.entity.Reader;
import com.library.librarymanagement.entity.SystemUser;
import com.library.librarymanagement.repository.ReaderRepository;
import com.library.librarymanagement.repository.SystemUserRepository;
import com.library.librarymanagement.repository.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileServiceImpl implements ProfileService {

    private final AccountRepository accountRepository;
    private final ReaderRepository readerRepository;
    private final SystemUserRepository systemUserRepository;
    private final PasswordEncoder passwordEncoder;
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    /** Lấy Account hiện tại từ SecurityContext (đã được JwtAuthenticationFilter set) */
    private Account getCurrentAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated");
        }
        String username = auth.getName();
        return accountRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found: " + username));
    }

    @Override
    public Object getMyProfile() {
        Account account = getCurrentAccount();
        Long accountId = account.getId();

        // Ưu tiên check Reader
        Optional<Reader> readerOpt = readerRepository.fetchDetailByAccountId(accountId);
        if (readerOpt.isPresent()) {
            Reader reader = readerOpt.get();
            Account a = reader.getAccount(); // đã được fetch kèm
            return ReaderDetailDto.builder()
                    .email(a.getEmail())
                    .fullName(a.getFullName())
                    .username(a.getUsername())
                    .phone(a.getPhone())
                    .status(a.getStatus())
                    .readerCode(reader.getReaderCode())
                    .build();
        }

        // Nếu không phải reader -> staff
        SystemUser su = systemUserRepository.fetchByAccountId(accountId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "SystemUser not found for accountId=" + accountId));

        Account a = su.getAccount();
        return StaffDetailDto.builder()
                .username(a.getUsername())
                .email(a.getEmail())
                .fullName(a.getFullName())
                .phone(a.getPhone())
                .status(a.getStatus())
                .position(su.getPosition())
                .joinDate(formatDate(su.getJoinDate()))
                .hireDate(formatDate(su.getHireDate()))
                .build();
    }

    @Override
    @Transactional
    public Object updateCurrentProfile(ProfileUpdateRequest req) {
        // 1) Lấy account hiện tại
        Account acc = getCurrentAccount();
        Long accountId = acc.getId();

        // 2) Validate email unique (nếu có rule)
        String newEmail = req.getEmail().trim();
        if (accountRepository.existsByEmailAndIdNot(newEmail, accountId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
        }

        // 3) Cập nhật CHỈ các field nằm ở Account
        acc.setFullName(req.getFullName().trim());
        acc.setEmail(newEmail);
        acc.setPhone(req.getPhone() == null ? null : req.getPhone().trim());
        accountRepository.save(acc);

        // 4) Trả về DTO theo role/thực-thể liên kết
        Optional<SystemUser> suOpt = systemUserRepository.fetchByAccountId(accountId);
        if (suOpt.isPresent()) {
            SystemUser su = suOpt.get();
            return StaffDetailDto.builder()
                    .username(acc.getUsername())
                    .email(acc.getEmail())
                    .fullName(acc.getFullName())
                    .phone(acc.getPhone())
                    .status(acc.getStatus())
                    .position(su.getPosition())
                    .joinDate(formatDate(su.getJoinDate()))
                    .hireDate(formatDate(su.getHireDate()))
                    .build();
        }

        Optional<Reader> rdOpt = readerRepository.fetchDetailByAccountId(accountId);
        if (rdOpt.isPresent()) {
            Reader rd = rdOpt.get();
            return ReaderDetailDto.builder()
                    .username(acc.getUsername())
                    .email(acc.getEmail())
                    .fullName(acc.getFullName())
                    .phone(acc.getPhone())
                    .status(acc.getStatus())
                    .readerCode(rd.getReaderCode())
                    .build();
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile record not found");
    }

    @Override
    @Transactional
    public Object changePassword(ChangePasswordRequest req) {
        // 1) Lấy account hiện tại
        Account acc = getCurrentAccount();

        String oldRaw = req.getOldPassword();
        String newRaw = req.getNewPassword();

        // 2) Kiểm tra mật khẩu cũ đúng không
        if (!passwordEncoder.matches(oldRaw, acc.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Old password is incorrect"
            );
        }

        // 3) BE validate thêm (dù FE đã làm)
        if (newRaw.length() < 8) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "New password must be at least 8 characters"
            );
        }

        // Không cho trùng mật khẩu cũ
        if (passwordEncoder.matches(newRaw, acc.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "New password must be different from old password"
            );
        }

        // 4) Encode & lưu
        acc.setPassword(passwordEncoder.encode(newRaw));
        accountRepository.save(acc);

        // 5) Trả message đơn giản cho FE (FE không cần data gì thêm)
        return java.util.Map.of("message", "Password changed successfully");
    }


    private String formatDate(Timestamp d) {
        if (d == null) return null;
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(ISO_DATE);
    }
}
