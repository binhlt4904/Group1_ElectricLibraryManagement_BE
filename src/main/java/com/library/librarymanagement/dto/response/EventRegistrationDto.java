package com.library.librarymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRegistrationDto {
    
    private Long id;
    private Long eventId;
    private String eventTitle;
    private Long accountId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String specialRequests;
    private String status; // REGISTERED, CANCELLED, ATTENDED
    private Timestamp registeredDate;
}
