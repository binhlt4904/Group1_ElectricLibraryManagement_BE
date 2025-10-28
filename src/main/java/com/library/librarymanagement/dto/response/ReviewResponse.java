package com.library.librarymanagement.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ReviewResponse {
    private Long id;
    private String note;
    private Integer rate;
    private Timestamp createdDate;
    private String reviewerName;

}
