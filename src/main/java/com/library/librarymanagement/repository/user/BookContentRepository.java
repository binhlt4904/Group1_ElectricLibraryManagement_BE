package com.library.librarymanagement.repository.user;

import com.library.librarymanagement.entity.BookContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookContentRepository extends JpaRepository<BookContent, Long> {
    List<BookContent> findByBookId(long bookId);
}
