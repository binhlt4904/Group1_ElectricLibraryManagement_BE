// src/main/java/com/library/librarymanagement/repository/CategoryRepository.java
package com.library.librarymanagement.repository;

import com.library.librarymanagement.entity.Category;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Tìm theo name chứa từ khóa và isDeleted cụ thể
    List<Category> findByNameContainingIgnoreCaseAndIsDeleted(String name, Boolean isDeleted);

    // Tìm theo name chứa từ khóa (bỏ qua isDeleted)
    List<Category> findByNameContainingIgnoreCase(String name);

    // Tìm tất cả theo isDeleted
    List<Category> findByIsDeleted(Boolean isDeleted);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String newName,Long id);
}
