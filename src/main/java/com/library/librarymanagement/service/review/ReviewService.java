package com.library.librarymanagement.service.review;


import com.library.librarymanagement.dto.response.ReviewResponse;
import com.library.librarymanagement.entity.Book;
import com.library.librarymanagement.entity.Reader;
import com.library.librarymanagement.entity.Review;
import com.library.librarymanagement.repository.ReaderRepository;
import com.library.librarymanagement.repository.review.ReviewRepository;
import com.library.librarymanagement.repository.user.BookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;

    /** 🔹 Lấy danh sách review theo bookId */
    public List<ReviewResponse> getBookReviews(Long bookId) {
        List<Review> reviews = reviewRepository.findAllByBookIdOrderByCreatedDateDesc(bookId);
        return reviews.stream().map(review -> {
            ReviewResponse reviewResponse = new ReviewResponse();
            reviewResponse.setId(review.getId());
            reviewResponse.setCreatedDate(review.getCreatedDate());
            reviewResponse.setRate(review.getRate());
            reviewResponse.setNote(review.getNote());

            // ✅ Nếu account null → dùng readerCode
            String reviewerName = (review.getReviewer().getAccount() != null)
                    ? review.getReviewer().getAccount().getFullName()
                    : review.getReviewer().getReaderCode();

            reviewResponse.setReviewerName(reviewerName);
            return reviewResponse;
        }).toList();
    }

    /** 🔹 Thêm review (chỉ cho phép USER) */
    public ReviewResponse addReview(Long bookId, Long readerId, String note, Integer rate, String roleName) {
        String roleStr = roleName == null ? "" : roleName.trim().toUpperCase();
        if (!(roleStr.equals("USER") || roleStr.equals("READER") || roleStr.equals("ROLE_USER") || roleStr.equals("3"))) {
            throw new RuntimeException("Only readers can write reviews");
        }


        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        Reader reader = readerRepository.findByAccountId(readerId)
                .orElseThrow(() -> new RuntimeException("Reader not found by accountId"));




        Review review = new Review();
        review.setBook(book);
        review.setReviewer(reader);
        review.setNote(note);
        review.setRate(rate);
        review.setCreatedDate(new Timestamp(System.currentTimeMillis()));

        Review saved = reviewRepository.save(review);

        String reviewerName = (reader.getAccount() != null)
                ? reader.getAccount().getFullName()
                : reader.getReaderCode();

        return new ReviewResponse(
                saved.getId(),
                saved.getNote(),
                saved.getRate(),
                saved.getCreatedDate(),
                reviewerName
        );
    }

    /** 🔹 Xóa review — USER chỉ xóa của mình, STAFF/ADMIN xóa được tất cả */
    public void deleteReview(Long reviewId, Long requesterId, String roleName) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // ✅ Kiểm tra reviewer và account trước khi so sánh
        boolean isOwner = false;
        if (review.getReviewer() != null && review.getReviewer().getAccount() != null) {
            Long reviewerAccountId = review.getReviewer().getAccount().getId();
            isOwner = reviewerAccountId.equals(requesterId);
        }

        boolean isStaff = "STAFF".equalsIgnoreCase(roleName) || "ROLE_STAFF".equalsIgnoreCase(roleName);
        boolean isAdmin = "ADMIN".equalsIgnoreCase(roleName) || "ROLE_ADMIN".equalsIgnoreCase(roleName);

        if (!(isAdmin || isStaff || isOwner)) {
            throw new RuntimeException("You are not allowed to delete this review");
        }

        reviewRepository.delete(review);
    }
    /** 🔹 Cập nhật review — USER chỉ sửa của mình, STAFF/ADMIN sửa được tất cả */
    public ReviewResponse updateReview(Long reviewId, Long requesterId, String newNote, Integer newRate, String roleName) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // ✅ Kiểm tra quyền
        boolean isOwner = review.getReviewer() != null
                && review.getReviewer().getAccount() != null
                && review.getReviewer().getAccount().getId().equals(requesterId);

        boolean isStaff = "STAFF".equalsIgnoreCase(roleName) || "ROLE_STAFF".equalsIgnoreCase(roleName);
        boolean isAdmin = "ADMIN".equalsIgnoreCase(roleName) || "ROLE_ADMIN".equalsIgnoreCase(roleName);

        if (!(isOwner || isStaff || isAdmin)) {
            throw new RuntimeException("You are not allowed to edit this review");
        }

        // ✅ Cập nhật nội dung
        review.setNote(newNote);
        review.setRate(newRate);
        review.setCreatedDate(new Timestamp(System.currentTimeMillis())); // cập nhật ngày chỉnh sửa

        Review saved = reviewRepository.save(review);

        String reviewerName = review.getReviewer().getAccount() != null
                ? review.getReviewer().getAccount().getFullName()
                : review.getReviewer().getReaderCode();

        return new ReviewResponse(
                saved.getId(),
                saved.getNote(),
                saved.getRate(),
                saved.getCreatedDate(),
                reviewerName
        );
    }


}
