package com.library.librarymanagement.service.category;

import com.library.librarymanagement.dto.request.CreateCatgoryRequest;
import com.library.librarymanagement.dto.request.UpdateCategoryRequest;
import com.library.librarymanagement.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories(String keyword, Boolean isDeleted);
    CategoryResponse createCategory(CreateCatgoryRequest req);
    CategoryResponse updateCategory(UpdateCategoryRequest req);
    CategoryResponse deleteCategory(Long id);
    CategoryResponse restoreCategory(Long id);
}
