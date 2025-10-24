package com.library.librarymanagement.repository.user;

import com.library.librarymanagement.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findAllByIsDeletedFalse( Pageable pageable);
}
