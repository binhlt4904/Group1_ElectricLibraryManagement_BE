package com.library.librarymanagement.service.category;

import com.library.librarymanagement.dto.request.CreateCatgoryRequest;
import com.library.librarymanagement.dto.request.UpdateCategoryRequest;
import com.library.librarymanagement.dto.response.CategoryResponse;
import com.library.librarymanagement.entity.Category;
import com.library.librarymanagement.repository.CategoryRepository;
import com.library.librarymanagement.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> getAllCategories(String keyword, Boolean isDeleted) {
        List<Category> categories;

        if (keyword != null && !keyword.trim().isEmpty() && isDeleted != null) {
            categories = categoryRepository.findByNameContainingIgnoreCaseAndIsDeleted(keyword.trim(), isDeleted);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            categories = categoryRepository.findByNameContainingIgnoreCase(keyword.trim());
        } else if (isDeleted != null) {
            categories = categoryRepository.findByIsDeleted(isDeleted);
        } else {
            categories = categoryRepository.findAll();
        }

        return categories.stream()
                .sorted((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate())) // sort mới nhất trước
                .map(c -> CategoryResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .createdDate(c.getCreatedDate())
                        .updatedDate(c.getUpdatedDate())
                        .isDeleted(c.getIsDeleted())
                        .createdBy(c.getCreatedBy())
                        .updatedBy(c.getUpdatedBy())
                        .build()
                ).toList();
    }

    @Override
    public CategoryResponse createCategory(CreateCatgoryRequest req) {
        String name = req.getName().trim();

        // Kiểm tra trùng tên
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new RuntimeException("Category name already exists");
        }

        Category category = new Category();
        category.setName(name);
        category.setCreatedDate(new Date());
        category.setIsDeleted(false);
        category.setCreatedBy(1L);
//        category.setUpdatedBy(1L);

        // Khi tạo Category, danh sách documents rỗng (1 category có thể có nhiều document sau này)
        categoryRepository.save(category);

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .createdDate(category.getCreatedDate())
                .updatedDate(category.getUpdatedDate())
                .isDeleted(category.getIsDeleted())
                .createdBy(category.getCreatedBy())
                .updatedBy(category.getUpdatedBy())
                .build();
    }

    @Override
    public CategoryResponse updateCategory(UpdateCategoryRequest req) {
        Category category = categoryRepository.findById(req.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        String newName = req.getName().trim();

        // Kiểm tra trùng tên với category khác
        boolean exists = categoryRepository.existsByNameIgnoreCaseAndIdNot(newName, req.getId());
        if (exists) {
            throw new RuntimeException("Category name already exists");
        }

        category.setName(newName);
        category.setUpdatedDate(new Date());
        category.setUpdatedBy(1L);

        categoryRepository.save(category);

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .createdDate(category.getCreatedDate())
                .updatedDate(category.getUpdatedDate())
                .isDeleted(category.getIsDeleted())
                .createdBy(category.getCreatedBy())
                .updatedBy(category.getUpdatedBy())
                .build();
    }

    @Override
    public CategoryResponse deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        if (Boolean.TRUE.equals(category.getIsDeleted())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category already deleted");
        }

        category.setIsDeleted(true);
        category.setUpdatedBy(1L);
        category.setUpdatedDate(new Date());
        categoryRepository.save(category);
        return CategoryResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .createdDate(category.getCreatedDate())
                    .updatedDate(category.getUpdatedDate())
                    .isDeleted(category.getIsDeleted())
                    .createdBy(category.getCreatedBy())
                    .updatedBy(category.getUpdatedBy())
                    .build();
    }

    @Override
    public CategoryResponse restoreCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        if (Boolean.FALSE.equals(category.getIsDeleted())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category is already active");
        }

        category.setIsDeleted(false);
        category.setUpdatedBy(1L);
        category.setUpdatedDate(new Date());
        categoryRepository.save(category);
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .createdDate(category.getCreatedDate())
                .updatedDate(category.getUpdatedDate())
                .isDeleted(category.getIsDeleted())
                .createdBy(category.getCreatedBy())
                .updatedBy(category.getUpdatedBy())
                .build();
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream().map(category -> {
            CategoryResponse response = new CategoryResponse();
            response.setId(category.getId());
            response.setName(category.getName());
            response.setCreatedDate(category.getCreatedDate());
            response.setUpdatedDate(category.getUpdatedDate());
            response.setIsDeleted(category.getIsDeleted());
            response.setCreatedBy(category.getCreatedBy());
            response.setUpdatedBy(category.getUpdatedBy());
            return response;
        }).toList();
    }

}

