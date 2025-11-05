package com.library.librarymanagement.repository;

import com.library.librarymanagement.entity.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReaderRepository extends JpaRepository<Reader, Long> {
    Optional<Reader> findByAccountId(Long accountId);
    // ReaderRepository.java
    @Query("""
   select r from Reader r
   join fetch r.account a
   where a.id = :accountId
""")
    Optional<Reader> fetchDetailByAccountId(@Param("accountId") Long accountId);

    boolean existsByReaderCode(String readerCode);
}
