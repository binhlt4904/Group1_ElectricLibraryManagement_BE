package com.library.librarymanagement.service.review;


import com.library.librarymanagement.dto.response.ReviewResponse;
import com.library.librarymanagement.entity.Review;
import com.library.librarymanagement.repository.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public List<ReviewResponse> getBookReviews(Long bookId) {
        List<Review> reviews = reviewRepository.findAllByBookId(bookId);
        return reviews.stream().map(review -> {
            ReviewResponse reviewResponse = new ReviewResponse();
            reviewResponse.setId(review.getId());
            reviewResponse.setCreatedDate(review.getCreatedDate());
            reviewResponse.setRate(review.getRate());
            reviewResponse.setNote(review.getNote());
            reviewResponse.setReviewerName(review.getReviewer().getReaderCode());
            return reviewResponse;
        }).toList();
    }
}
