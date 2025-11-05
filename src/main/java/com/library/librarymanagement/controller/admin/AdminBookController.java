package com.library.librarymanagement.controller.admin;

import com.library.librarymanagement.dto.request.BookRequest;
import com.library.librarymanagement.dto.request.BookUpdateRequest;
import com.library.librarymanagement.dto.response.BookContentResponse;
import com.library.librarymanagement.dto.response.BookResponse;
import com.library.librarymanagement.entity.Book;
import com.library.librarymanagement.entity.BookContent;
import com.library.librarymanagement.service.book.BookService;
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
@RequestMapping("/api/v1/public/admin/books")
public class AdminBookController {
    private final BookService bookService;

    @GetMapping(path="/")
    public ResponseEntity<?> getAllBooks(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "4") int size,
                                         @RequestParam(required = false) String search,
                                         @RequestParam(required = false) String category,
                                         @RequestParam(required = false) String status) {
        System.out.println("Page: " + page + ", Size: " + size);
        System.out.println("Fetching books with search: " + search + ", category: " + category + ", status: " + status);
        Pageable pageable = PageRequest.of(page, size, Sort.by("importedDate").descending());
        Page<BookResponse> books = bookService.getBooks(pageable, search, category, status);

        return ResponseEntity.ok(books);
    }

    @PostMapping(path="/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createBook(@ModelAttribute BookRequest book) throws IOException {
        System.out.println("Creating Book: " + book);
        bookService.createBook(book);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path="/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody BookUpdateRequest book) {
        System.out.println("Id: " + id);
        System.out.println("book: "+ book);
        System.out.println("Updating Book ID " + id + " with data: " + book);
        bookService.updateBook(id, book);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/contents")
    public ResponseEntity<List<BookContentResponse>> getBookContents(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookContent(id));
    }

}
