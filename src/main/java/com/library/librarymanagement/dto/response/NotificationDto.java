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
public class NotificationDto {

    private Long id;
    private String title;
    private String description;
    private String notificationType;
    private Boolean isRead;
    private Timestamp createdDate;
    private Timestamp readDate;
    private Long toUserId;
    private String toUsername;
    private Long relatedBookId;
    private Long relatedEventId;
    private Long relatedBorrowRecordId;
}

