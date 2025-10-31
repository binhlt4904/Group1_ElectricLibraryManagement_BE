package com.library.librarymanagement.repository.borrow;

import com.library.librarymanagement.entity.LibraryCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibraryCardRepository extends JpaRepository<LibraryCard, Long> {

    Optional<LibraryCard> findByReader_Id(Long id);
}
