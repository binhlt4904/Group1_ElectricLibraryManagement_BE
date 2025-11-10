package com.library.librarymanagement.service.profile;

import com.library.librarymanagement.dto.request.ChangePasswordRequest;
import com.library.librarymanagement.dto.request.ProfileUpdateRequest;

public interface ProfileService {
    Object getMyProfile();
    Object updateCurrentProfile(ProfileUpdateRequest req);
    Object changePassword(ChangePasswordRequest req);
}
