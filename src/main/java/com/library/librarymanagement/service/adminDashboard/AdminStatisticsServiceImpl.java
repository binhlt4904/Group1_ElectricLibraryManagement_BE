package com.library.librarymanagement.service.adminDashboard;

import com.library.librarymanagement.dto.response.admin_dashboard.*;
import com.library.librarymanagement.entity.Event;
import com.library.librarymanagement.repository.admin_dashboard.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminStatisticsServiceImpl implements AdminStatisticsService {

    private final WalletTransactionDashboardRepository walletTransactionRepository;
    private final BookDashboardRepository bookDashboardRepository;
    private final AccountDashboardRepository accountDashboardRepository;
    private final BorrowRecordDashboardRepository borrowRecordDashboardRepository;
    private final EventDashboardRepository eventDashboardRepository;
    @Override
    public TotalRevenueResponse getTotalRevenue() {
        BigDecimal total = walletTransactionRepository.sumAmountByTypeIncreasedAndStatusDone();
        if (total == null) {
            total = BigDecimal.ZERO;
        }
        return new TotalRevenueResponse(total);
    }

    @Override
    public TotalBookResponse getTotalBooks() {
        long totalBooks = bookDashboardRepository.countByIsDeletedFalse();
        // Nếu không có isDeleted thì sửa thành: long totalBooks = bookDashboardRepository.count();
        return new TotalBookResponse(totalBooks);
    }

    @Override
    public TotalActiveReaderResponse getActiveReaders() {
        long count = accountDashboardRepository
                .countByRole_IdAndStatus(3L, "ACTIVE"); // role id = 3, status = "ACTIVE"
        return new TotalActiveReaderResponse(count);
    }

    @Override
    public CurrentBorrowalsResponse getCurrentBorrowals() {
        long count = borrowRecordDashboardRepository.countCurrentBorrowals();
        // Nếu dùng status string thì đổi thành:
        // long count = borrowRecordDashboardRepository.countByStatus("BORROWING");
        return new CurrentBorrowalsResponse(count);
    }

    @Override
    public OverdueItemsResponse getOverdueItems() {
        LocalDate today = LocalDate.now();
        Date todayDate = Date.valueOf(today); // java.sql.Date (extends java.util.Date)
        long count = borrowRecordDashboardRepository.countOverdueItems(todayDate);
        return new OverdueItemsResponse(count);
    }
    @Override
    public List<PopularBookResponse> getPopularBooks() {
        return borrowRecordDashboardRepository.findTopBorrowedBooks(PageRequest.of(0, 5));
    }

    @Override
    public List<RecentActivityResponse> getRecentActivities() {
        List<Event> events = eventDashboardRepository.findTop10ByOrderByCreatedDateDesc();

        return events.stream()
                .map(e -> new RecentActivityResponse(
                        e.getId(),
                        e.getTitle(),
                        e.getDescription(),
                        // lấy tên user từ quan hệ ManyToOne SystemUser fromUser
                        e.getFromUser() != null ? e.getFromUser().getAccount().getFullName() : null,
                        e.getCreatedDate()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowingTrendResponse> getBorrowingTrendsCurrentYear() {
        int currentYear = LocalDate.now().getYear();

        // Lấy các tháng có dữ liệu từ DB (ví dụ trả về tháng 2,3,5,...)
        List<BorrowingTrendResponse> fromDb =
                borrowRecordDashboardRepository.getMonthlyBorrowingTrends(currentYear);

        // Đưa về map<month, count> cho dễ fill 12 tháng
        Map<Integer, Long> monthToCount = fromDb.stream()
                .collect(Collectors.toMap(
                        BorrowingTrendResponse::getMonth,
                        BorrowingTrendResponse::getBorrowCount
                ));

        // Luôn trả đủ 12 tháng, tháng không có dữ liệu → 0
        List<BorrowingTrendResponse> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            long count = monthToCount.getOrDefault(month, 0L);
            result.add(new BorrowingTrendResponse(month, count));
        }

        return result;
    }
}
