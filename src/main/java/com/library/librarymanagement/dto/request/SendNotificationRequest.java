package com.library.librarymanagement.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendNotificationRequest {
    private String title;
    private String description; // message body
    private String notificationType; // e.g., NEW_EVENT, CARD_SUSPENDED
    private Long toUserId;
    private Long relatedBookId;
    private Long relatedEventId;
    private Long relatedBorrowRecordId;
}
