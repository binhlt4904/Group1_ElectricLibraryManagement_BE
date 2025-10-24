package com.library.librarymanagement.repository.admin;

import com.library.librarymanagement.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminBookRepository extends JpaRepository<Book, Long> {
    Page<Book> findAll(Pageable pageable);
}
