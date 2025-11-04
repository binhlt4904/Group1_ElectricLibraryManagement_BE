package com.library.librarymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private String note;
    private Integer rate;
    private Timestamp createdDate;
    private String reviewerName;
    private Long reviewerId;

}
