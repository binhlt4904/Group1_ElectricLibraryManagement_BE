package com.library.librarymanagement.controller.user;

import com.library.librarymanagement.dto.request.BookRequest;
import com.library.librarymanagement.dto.response.BookContentResponse;
import com.library.librarymanagement.dto.response.BookResponse;
import com.library.librarymanagement.entity.Book;
import com.library.librarymanagement.entity.BookContent;
import com.library.librarymanagement.service.book.BookService;
import com.library.librarymanagement.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<?> getAllBooks(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "4") int size,
                                         @RequestParam(required = false) String search,
                                         @RequestParam(required = false) String category,
                                         @RequestParam(defaultValue = "title") String sortBy,
                                         @RequestParam(defaultValue = "asc") String direction
                                         ) {
        System.out.println("SortBy: " + sortBy + ", Direction: " + direction);
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size,sort);
        Page<BookResponse> books = bookService.getAllBooks(pageable, search, category);

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

    @GetMapping("/{id}/contents/user")
    public ResponseEntity<List<BookContentResponse>> getBookContentsUser(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookContent(id));
    }

    @GetMapping("/{id}/related" )
    public ResponseEntity<List<BookResponse>> getRelatedBooks(
            @PathVariable Long id
    ) {
        List<BookResponse> relatedBooks = bookService.getRelatedBook(id);
        return ResponseEntity.ok(relatedBooks);
    }


}
