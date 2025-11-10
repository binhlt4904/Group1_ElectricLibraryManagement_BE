package com.library.librarymanagement.controller.admin;


import com.library.librarymanagement.dto.response.BookContentResponse;
import com.library.librarymanagement.entity.BookContent;
import com.library.librarymanagement.service.book.BookContentService;
import com.library.librarymanagement.service.book.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/book-contents")
@RequiredArgsConstructor
public class AdminBookContentController {
    private final BookContentService bookContentService;
    private final BookService bookService;

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookContentResponse> create(
            @RequestParam Long bookId,
            @RequestParam String title,
            @RequestParam Integer chapter,
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        System.out.println(bookId);

        BookContent saved = bookContentService.create(bookId, title, chapter, file);

        BookContentResponse response = new BookContentResponse();
        response.setId(saved.getId());
        response.setTitle(saved.getTitle());
        response.setContent(saved.getContent());
        response.setChapter(saved.getChapter().toString());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PatchMapping( path = "/{contentId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> update(
            @PathVariable Long contentId,
            @RequestParam (required = false) String title,
            @RequestParam (required = false) Integer chapter,
            @RequestParam (value = "file",required = false) MultipartFile file,
            @RequestParam (required = false) Boolean isDeleted
    ) throws Exception {

        BookContent saved = bookContentService.update(contentId, title, chapter, file, isDeleted);

        List<BookContentResponse> contents = bookService.getBookContent(saved.getBook().getId());


        return ResponseEntity.status(HttpStatus.CREATED).body(contents);
    }
}
