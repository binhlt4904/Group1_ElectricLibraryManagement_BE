package com.library.librarymanagement.dto.response.admin_dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OverdueItemsResponse {
    private long overdueItems;
}
