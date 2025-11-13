package com.library.librarymanagement.repository.user;

import com.library.librarymanagement.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book,Long>, JpaSpecificationExecutor<Book> {
    Page<Book> findAllByIsDeletedFalse(Pageable pageable);

    Optional<Book> findByBookCode(String bookCode);

    Book getBookById(Long id);
}
