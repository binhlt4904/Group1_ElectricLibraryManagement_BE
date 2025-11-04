package com.library.librarymanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @Size(max = 500, message = "Location must not exceed 500 characters")
    private String location;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    private Integer capacity;

    @Size(max = 50, message = "Status must not exceed 50 characters")
    private String status;

    // Image file for event banner
    private MultipartFile image;
}
