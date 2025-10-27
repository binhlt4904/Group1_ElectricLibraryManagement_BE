package com.library.librarymanagement.controller.admin;

import com.library.librarymanagement.dto.request.BookRequest;
import com.library.librarymanagement.dto.response.BookResponse;
import com.library.librarymanagement.entity.Book;
import com.library.librarymanagement.service.book.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/admin/books")
public class AdminBookController {
    private final BookService bookService;

    @GetMapping(path="/")
    public ResponseEntity<?> getAllBooks() {
        List<BookResponse> books = bookService.getAllAdminBooks();
        System.out.println("Fetched Books: " + books);

        return ResponseEntity.ok(books);
    }

    @PostMapping(path="/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createBook(@ModelAttribute BookRequest book) throws IOException {
        System.out.println("Creating Book: " + book);
        Book createdBook = bookService.createBook(book);
        return ResponseEntity.ok().body(createdBook);
    }

}
