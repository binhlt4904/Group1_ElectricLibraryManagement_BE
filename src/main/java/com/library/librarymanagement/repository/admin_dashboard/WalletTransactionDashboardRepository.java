package com.library.librarymanagement.repository.admin_dashboard;

import com.library.librarymanagement.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface WalletTransactionDashboardRepository extends JpaRepository<WalletTransaction, Long> {

    @Query("SELECT COALESCE(SUM(wt.amount), 0) " +
            "FROM WalletTransaction wt " +
            "WHERE wt.type = 'INCREASED' ")
    BigDecimal sumAmountByTypeIncreasedAndStatusDone();
}
