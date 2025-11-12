package com.library.librarymanagement.controller.user;

import com.library.librarymanagement.dto.response.BookContentResponse;
import com.library.librarymanagement.entity.BookContent;
import com.library.librarymanagement.service.book.BookContentService;
import com.library.librarymanagement.service.book.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/book-contents")
@RequiredArgsConstructor
public class BookContentController {
    private final BookContentService bookContentService;
    private final BookService bookService;



    @GetMapping("/{bookId}/{chapterId}")
    @PreAuthorize("hasRole('READER')")
    public ResponseEntity<BookContentResponse> get(@PathVariable Long bookId, @PathVariable Integer chapterId) throws Exception {
        BookContent content = bookContentService.getBookContent(bookId, chapterId);
        BookContentResponse response = new BookContentResponse();
        response.setId(content.getId());
        response.setTitle(content.getTitle());
        response.setContent(content.getContent());
        response.setChapter(content.getChapter().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/contents")
    public ResponseEntity<List<BookContentResponse>> getBookContents(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookContentUser(id));
    }
}
