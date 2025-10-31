package com.library.librarymanagement.controller.user;

import com.library.librarymanagement.dto.request.BookRequest;
import com.library.librarymanagement.dto.response.BookContentResponse;
import com.library.librarymanagement.dto.response.BookResponse;
import com.library.librarymanagement.entity.Book;
import com.library.librarymanagement.entity.BookContent;
import com.library.librarymanagement.service.book.BookService;
import com.library.librarymanagement.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/books")
public class BookController {
    private final BookService bookService;
    private final ReviewService reviewService;

    @GetMapping(path="/")
    public ResponseEntity<?> getAllBooks() {
        List<BookResponse> books = bookService.getAllBooks();
        System.out.println("Fetched Books: " + books);

        return ResponseEntity.ok(books);
    }


    @GetMapping(path="/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping("/{id}/contents")
    public ResponseEntity<List<BookContentResponse>> getBookContents(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookContentUser(id));
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<?> getBookReviews(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getBookReviews(id));
    }



}
