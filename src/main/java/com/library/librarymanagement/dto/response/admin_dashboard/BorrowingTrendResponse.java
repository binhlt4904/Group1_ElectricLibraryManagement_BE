package com.library.librarymanagement.dto.response.admin_dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BorrowingTrendResponse {

    // tháng (1–12)
    private int month;

    // số lượt mượn trong tháng
    private long borrowCount;
}
