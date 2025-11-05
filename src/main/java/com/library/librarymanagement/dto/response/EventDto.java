package com.library.librarymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private Long id;
    private String title;
    private String description;
    private LocalDate eventDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private String category;
    private Integer capacity;
    private Integer registered;
    private String status;
    private String imageUrl;
    private Date createdDate;
    private Date updatedDate;
    private Long createdBy;
    private String createdByName; // Organizer username
    private String organizerFullName; // Organizer full name
    private String organizerEmail; // Organizer email
}
