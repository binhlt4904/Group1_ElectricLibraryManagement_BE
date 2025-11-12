package com.library.librarymanagement.repository.review;

import com.library.librarymanagement.dto.response.ReviewResponse;
import com.library.librarymanagement.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByBookIdOrderByCreatedDateDesc(Long bookId);

    List<Review> findAllByBookId(long id);

    @Query("select coalesce(avg(r.rate), 0) from Review r where r.book.id = :bookId")
    Double avgRateByBookId(@Param("bookId") Long bookId);

    @Query("select count(r) from Review r where r.book.id = :bookId")
    Long countByBookId(@Param("bookId") Long bookId);
}
