package com.library.librarymanagement.controller.user;

import com.library.librarymanagement.dto.request.BookRequest;
import com.library.librarymanagement.dto.request.ReviewRequest;
import com.library.librarymanagement.dto.response.BookContentResponse;
import com.library.librarymanagement.dto.response.BookResponse;
import com.library.librarymanagement.dto.response.ReviewResponse;
import com.library.librarymanagement.entity.Book;
import com.library.librarymanagement.entity.BookContent;
import com.library.librarymanagement.service.book.BookService;
import com.library.librarymanagement.service.custom_user_details.CustomUserDetails;
import com.library.librarymanagement.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

//    @GetMapping("/{id}/contents/user")
//    public ResponseEntity<List<BookContentResponse>> getBookContentsUser(@PathVariable Long id) {
//        return ResponseEntity.ok(bookService.getBookContent(id));
//    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<?> addReview(@PathVariable Long id, @RequestBody ReviewRequest req) {
        System.out.println("📥 RECEIVED ADD REVIEW: " + req);

        return ResponseEntity.ok(
                reviewService.addReview(id, req.getReaderId(), req.getNote(), req.getRate(), req.getRoleName())
        );
    }


    /** 🔹 Xoá review (USER chỉ được xoá của mình, STAFF/ADMIN xoá được tất cả) */
    @DeleteMapping("/{bookId}/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long bookId,
            @PathVariable Long reviewId,
            @RequestParam Long requesterId,
            @RequestParam String role
    ) {
        reviewService.deleteReview(reviewId, requesterId, role);
        return ResponseEntity.ok(Map.of("message", "Review deleted successfully"));
    }

    @PutMapping("/{bookId}/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long bookId,
            @PathVariable Long reviewId,
            @RequestBody ReviewRequest request,
            @RequestParam Long requesterId,
            @RequestParam String role
    ) {
        ReviewResponse response = reviewService.updateReview(
                reviewId,
                requesterId,
                request.getNote(),
                request.getRate(),
                role
        );
        return ResponseEntity.ok(response);
    }




    /** 🔹 Lấy danh sách nội dung dạng “user” (ẩn chi tiết nội dung) */
    @GetMapping("/{id}/contents/user")
    public ResponseEntity<List<BookContentResponse>> getBookContentsUser(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookContent(id));
    }
}
