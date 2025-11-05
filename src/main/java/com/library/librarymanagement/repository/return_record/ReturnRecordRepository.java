package com.library.librarymanagement.repository.return_record;

import com.library.librarymanagement.entity.BorrowRecord;
import com.library.librarymanagement.entity.ReturnRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnRecordRepository extends JpaRepository<ReturnRecord, Long>, JpaSpecificationExecutor<ReturnRecord> {
}
