package com.library.librarymanagement.controller.user;


import com.library.librarymanagement.dto.request.ChangePasswordRequest;
import com.library.librarymanagement.dto.request.ProfileUpdateRequest;
import com.library.librarymanagement.service.profile.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {
        // lấy danh tính từ SecurityContext

        return ResponseEntity.ok(profileService.getMyProfile());
    }

    @PutMapping("/edit")
    public ResponseEntity<?> updateMyProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(profileService.updateCurrentProfile(request));
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(profileService.changePassword(request));
    }
}
