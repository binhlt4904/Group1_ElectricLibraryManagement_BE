package com.library.librarymanagement.repository.admin;

import com.library.librarymanagement.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    /**
     * Find all non-deleted documents with optional search and category filter
     */
    @Query("""
           SELECT d
           FROM Document d
           WHERE d.isDeleted = false
             AND (:title IS NULL OR LOWER(d.title) LIKE LOWER(CONCAT('%', :title, '%')))
             AND (:categoryName IS NULL OR d.category.name = :categoryName)
           """)
    Page<Document> findAllDocuments(
            @Param("title") String title,
            @Param("categoryName") String categoryName,
            Pageable pageable
    );

    /**
     * Find a document by ID that is not deleted
     */
    @Query("""
           SELECT d
           FROM Document d
           WHERE d.id = :id AND d.isDeleted = false
           """)
    Optional<Document> findByIdAndNotDeleted(@Param("id") Long id);
}

