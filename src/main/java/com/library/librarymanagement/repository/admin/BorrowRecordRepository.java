package com.library.librarymanagement.repository.admin;

import com.library.librarymanagement.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    /**
     * Find all borrow records due on a specific date
     */
    @Query("SELECT br FROM BorrowRecord br WHERE CAST(br.allowedDate AS date) = :dueDate AND br.returnRecord IS NULL")
    List<BorrowRecord> findBorrowRecordsDueOnDate(@Param("dueDate") Date dueDate);

    /**
     * Find all borrow records due in a specific number of days
     */
    @Query("SELECT br FROM BorrowRecord br WHERE CAST(br.allowedDate AS date) = :dueDate AND br.returnRecord IS NULL")
    List<BorrowRecord> findBorrowRecordsDueInDays(@Param("dueDate") LocalDate dueDate);

    /**
     * Find all overdue borrow records (allowedDate is before today)
     */
    @Query("SELECT br FROM BorrowRecord br WHERE CAST(br.allowedDate AS date) < :today AND br.returnRecord IS NULL")
    List<BorrowRecord> findOverdueBorrowRecords(@Param("today") LocalDate today);

    /**
     * Find all unreturned borrow records for a specific user
     */
    @Query("SELECT br FROM BorrowRecord br WHERE br.libraryCard.reader.account.id = :userId AND br.returnRecord IS NULL")
    List<BorrowRecord> findUnreturnedBorrowRecordsByUserId(@Param("userId") Long userId);

    /**
     * Find all borrow records for a specific user
     */
    @Query("SELECT br FROM BorrowRecord br WHERE br.libraryCard.reader.account.id = :userId ORDER BY br.borrowedDate DESC")
    List<BorrowRecord> findBorrowRecordsByUserId(@Param("userId") Long userId);

    /**
     * Find all borrow records for a specific book
     */
    @Query("SELECT br FROM BorrowRecord br WHERE br.book.id = :bookId ORDER BY br.borrowedDate DESC")
    List<BorrowRecord> findBorrowRecordsByBookId(@Param("bookId") Long bookId);
}

