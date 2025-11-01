package com.library.librarymanagement.controller.user;

import com.library.librarymanagement.dto.response.BookContentResponse;
import com.library.librarymanagement.entity.BookContent;
import com.library.librarymanagement.service.book.BookContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/public/book-contents")
@RequiredArgsConstructor
public class BookContentController {
    private final BookContentService bookContentService;
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

    @GetMapping("/{bookId}/{chapterId}")
    public ResponseEntity<BookContentResponse> get(@PathVariable Long bookId, @PathVariable Integer chapterId) throws Exception {
        BookContent content = bookContentService.getBookContent(bookId, chapterId);
        BookContentResponse response = new BookContentResponse();
        response.setId(content.getId());
        response.setTitle(content.getTitle());
        response.setContent(content.getContent());
        response.setChapter(content.getChapter().toString());
        return ResponseEntity.ok(response);
    }
}
