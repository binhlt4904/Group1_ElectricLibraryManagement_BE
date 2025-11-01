package com.library.librarymanagement.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {
    private Long readerId;
    private String note;
    private Integer rate;
    @JsonProperty("roleName")
    private String roleName;
}
