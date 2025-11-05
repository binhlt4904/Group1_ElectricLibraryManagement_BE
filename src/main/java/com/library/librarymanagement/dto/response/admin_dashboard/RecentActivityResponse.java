package com.library.librarymanagement.dto.response.admin_dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentActivityResponse {

    private Long id;

    private String title;

    private String description;

    // cột from_user trong DB → field fromUser trong entity/DTO
    private String fromUser;

    // created_date
    private Date createdDate;
}
