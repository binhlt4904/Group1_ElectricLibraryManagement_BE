package com.library.librarymanagement.service.adminDashboard;

import com.library.librarymanagement.dto.response.admin_dashboard.RecentActivityResponse;
import com.library.librarymanagement.dto.response.admin_dashboard.*;

import java.util.List;

public interface AdminStatisticsService {

    // sau này có thể thêm các hàm khác cho các card / chart
    TotalRevenueResponse getTotalRevenue();

    TotalBookResponse getTotalBooks();

    public TotalActiveReaderResponse getActiveReaders();

    CurrentBorrowalsResponse getCurrentBorrowals();

    OverdueItemsResponse getOverdueItems();

    List<PopularBookResponse> getPopularBooks();

    List<RecentActivityResponse> getRecentActivities();

    List<BorrowingTrendResponse> getBorrowingTrendsCurrentYear();
}
