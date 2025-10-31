package com.library.librarymanagement.repository.user;

import com.library.librarymanagement.entity.BookContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookContentRepository extends JpaRepository<BookContent, Long> {
    List<BookContent> findByBookId(long bookId);

    Optional<BookContent> findBookContentByBookIdAndChapter(long bookId, int chapter);
}
