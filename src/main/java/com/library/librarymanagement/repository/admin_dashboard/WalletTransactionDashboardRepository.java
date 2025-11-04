package com.library.librarymanagement.repository.admin_dashboard;

import com.library.librarymanagement.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface WalletTransactionDashboardRepository extends JpaRepository<WalletTransaction, Long> {

    // Nếu status là String trong entity (giống dữ liệu INCREASED / DECREASED trong DB)
    @Query("SELECT COALESCE(SUM(wt.amount), 0) " +
            "FROM WalletTransaction wt " +
            "WHERE wt.status = 'INCREASED'")
    BigDecimal sumAmountByStatusIncreased();

    /*
    // Nếu bạn dùng enum WalletTransactionStatus thì có thể viết generic hơn:
    @Query("SELECT COALESCE(SUM(wt.amount), 0) " +
           "FROM WalletTransaction wt " +
           "WHERE wt.status = :status")
    BigDecimal sumAmountByStatus(@Param("status") WalletTransactionStatus status);
    */
}
