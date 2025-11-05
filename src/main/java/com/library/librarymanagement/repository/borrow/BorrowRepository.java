package com.library.librarymanagement.repository.borrow;

import com.library.librarymanagement.entity.Book;
import com.library.librarymanagement.entity.BorrowRecord;
import com.library.librarymanagement.entity.LibraryCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRepository extends JpaRepository<BorrowRecord, Long>, JpaSpecificationExecutor<BorrowRecord> {

    @Query("SELECT b FROM BorrowRecord b " +
            "WHERE b.status IN ('Borrowed') " +
            "AND (b.libraryCard.cardNumber = :cardNumber) " +
            "AND (b.book.id = :bookId)")
    Optional<BorrowRecord> findActiveBorrowRecordByLibraryCardAndBook(@Param("cardNumber") String cardNumber,@Param("bookId") Long bookId);

    @EntityGraph(attributePaths = {
            "returnRecord"
    })
    Page<BorrowRecord> findAll(Specification<BorrowRecord> spec, Pageable pageable);

    @Modifying
    @Query("UPDATE BorrowRecord b SET b.status = 'Overdue' WHERE b.status = 'Borrowed' AND b.allowedDate < :currentDate")
    int markOverdueRecords(Date currentDate);

//    @Query("SELECT b FROM BorrowRecord b WHERE b.status = 'Borrowed' AND b.allowedDate = :tomorrow")
//    List<BorrowRecord> findRecordsDueTomorrow(Date tomorrow);
    @Query("""
        SELECT b FROM BorrowRecord b
        WHERE b.status = 'Borrowed'
        AND b.allowedDate >= :startOfTomorrow
        AND b.allowedDate < :endOfTomorrow
    """)
    List<BorrowRecord> findRecordsDueTomorrow(
            @Param("startOfTomorrow") Date startOfTomorrow,
            @Param("endOfTomorrow") Date endOfTomorrow
    );
}
