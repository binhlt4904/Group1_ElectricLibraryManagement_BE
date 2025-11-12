package com.library.librarymanagement.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class NotificationDto {
    private Long id;
    private String title;
    private String description;
    private String message;
    private String notificationType;
    private String type;
    private Boolean isRead;
    private Boolean read;
    private Instant createdDate;
    private Instant createdAt;
    private Long relatedBookId;
    private Long relatedEventId;
    private Long relatedBorrowRecordId;
    private Long relatedCardId;
}
