package com.library.librarymanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendNotificationRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Notification type is required")
    private String notificationType; // NEW_BOOK, NEW_EVENT, REMINDER, OVERDUE

    @NotNull(message = "Target user ID is required")
    private Long toUserId;

    private Long relatedBookId;
    private Long relatedEventId;
    private Long relatedBorrowRecordId;
}

