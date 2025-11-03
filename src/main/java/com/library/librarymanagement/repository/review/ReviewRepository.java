package com.library.librarymanagement.repository.review;

import com.library.librarymanagement.dto.response.ReviewResponse;
import com.library.librarymanagement.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByBookIdOrderByCreatedDateDesc(Long bookId);

    List<Review> findAllByBookId(long id);
}
