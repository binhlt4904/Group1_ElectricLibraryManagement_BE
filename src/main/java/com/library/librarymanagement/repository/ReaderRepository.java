package com.library.librarymanagement.repository;

import com.library.librarymanagement.entity.Reader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReaderRepository extends JpaRepository<Reader, Long> {
    Optional<Reader> findByAccountId(Long accountId);
}
