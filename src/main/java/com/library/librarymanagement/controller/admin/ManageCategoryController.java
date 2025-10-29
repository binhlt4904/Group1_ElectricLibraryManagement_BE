package com.library.librarymanagement.controller.admin;

import com.library.librarymanagement.dto.request.CreateCatgoryRequest;
import com.library.librarymanagement.dto.request.UpdateCategoryRequest;
import com.library.librarymanagement.dto.response.CategoryResponse;
import com.library.librarymanagement.service.category.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class ManageCategoryController {

    private final CategoryService categoryService;

    @GetMapping("categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CategoryResponse>> getAllCategories(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isDeleted
    ) {
        List<CategoryResponse> result = categoryService.getAllCategories(keyword, isDeleted);
        return ResponseEntity.ok(result);
    }
    @PostMapping("categories/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCatgoryRequest req) {
        CategoryResponse created = categoryService.createCategory(req);
        return ResponseEntity.ok(created);
    }
    @PutMapping("categories/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory(@Valid @RequestBody UpdateCategoryRequest req) {
        CategoryResponse updated = categoryService.updateCategory(req);
        return ResponseEntity.ok(updated);
    }
    @DeleteMapping("categories/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> deleteCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.deleteCategory(id));
    }
    @PutMapping("categories/restore/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> restoreCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.restoreCategory(id));
    }
}
