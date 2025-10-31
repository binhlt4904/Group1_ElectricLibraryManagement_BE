package com.library.librarymanagement.repository.borrow;

import com.library.librarymanagement.entity.Book;
import com.library.librarymanagement.entity.BorrowRecord;
import com.library.librarymanagement.entity.LibraryCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BorrowRepository extends JpaRepository<BorrowRecord, Long> {

    @Query("SELECT b FROM BorrowRecord b " +
            "WHERE b.status IN ('Borrowed') " +
            "AND (b.libraryCard.cardNumber = :cardNumber) " +
            "AND (b.book.id = :bookId)")
    Optional<BorrowRecord> findActiveBorrowRecordByLibraryCardAndBook(@Param("cardNumber") String cardNumber,@Param("bookId") Long bookId);
}
