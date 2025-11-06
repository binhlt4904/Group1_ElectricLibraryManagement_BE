package com.library.librarymanagement.repository.author;

import com.library.librarymanagement.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByFullName(String fullName);

    @Query("SELECT a FROM Author a " +
            "WHERE LOWER(a.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(a.nationality) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Author> searchAuthors(String keyword, Pageable pageable);
}
