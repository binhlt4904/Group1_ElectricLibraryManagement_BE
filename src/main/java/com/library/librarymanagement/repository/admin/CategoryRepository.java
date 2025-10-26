package com.library.librarymanagement.repository.admin;

import com.library.librarymanagement.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find a category by name
     */
    Optional<Category> findByName(String name);
}

