package com.library.librarymanagement.repository.admin_dashboard;

import com.library.librarymanagement.dto.response.admin_dashboard.BorrowingTrendResponse;
import com.library.librarymanagement.dto.response.admin_dashboard.PopularBookResponse;
import com.library.librarymanagement.entity.BorrowRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface BorrowRecordDashboardRepository extends JpaRepository<BorrowRecord, Long> {

    //   private ReturnRecord returnRecord;
    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.returnRecord IS NULL")
    long countCurrentBorrowals();

    // Tổng số lượt đang quá hạn (allowedDate < today và chưa trả)
    @Query("SELECT COUNT(br) " +
            "FROM BorrowRecord br " +
            "WHERE br.allowedDate < :today " +
            "AND br.returnRecord IS NULL")
    long countOverdueItems(@Param("today") Date today);

    @Query("SELECT new com.library.librarymanagement.dto.response.admin_dashboard.PopularBookResponse(" +
            " b.id, b.title, a.fullName, COUNT(br.id)) " +
            "FROM BorrowRecord br " +
            "JOIN br.book b " +
            "JOIN b.author a " +
            "WHERE b.isDeleted = false " +
            "GROUP BY b.id, b.title, a.fullName " +
            "ORDER BY COUNT(br.id) DESC")
    List<PopularBookResponse> findTopBorrowedBooks(Pageable pageable);

    @Query("SELECT new com.library.librarymanagement.dto.response.admin_dashboard.BorrowingTrendResponse(" +
            " FUNCTION('MONTH', br.borrowedDate), " +
            " COUNT(br.id)) " +
            "FROM BorrowRecord br " +
            "WHERE FUNCTION('YEAR', br.borrowedDate) = :year " +
            "GROUP BY FUNCTION('MONTH', br.borrowedDate) " +
            "ORDER BY FUNCTION('MONTH', br.borrowedDate)")
    List<BorrowingTrendResponse> getMonthlyBorrowingTrends(@Param("year") int year);
}
