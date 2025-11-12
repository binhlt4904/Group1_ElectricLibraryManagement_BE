package com.library.librarymanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRegistrationRequest {
    private String fullName;
    private String email;
    private String phone;
    private String specialRequests;
}
