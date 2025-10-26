package com.library.librarymanagement.controller.user;

import com.library.librarymanagement.dto.response.BookResponse;
import com.library.librarymanagement.entity.Book;
import com.library.librarymanagement.service.book.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/books")
public class BookController {
    private final BookService bookService;

    @GetMapping("/")
    public ResponseEntity<?> getAllBooks() {
        List<BookResponse> books = bookService.getAllBooks();
        System.out.println("Fetched Books: " + books);

        return ResponseEntity.ok(books);
    }
}
