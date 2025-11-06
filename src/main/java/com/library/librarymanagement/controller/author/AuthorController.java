package com.library.librarymanagement.controller.author;

import com.library.librarymanagement.dto.request.AuthorRequest;
import com.library.librarymanagement.dto.response.AuthorResponse;
import com.library.librarymanagement.service.author.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/authors")
public class AuthorController {
    private final AuthorService authorService;
    // 🔹 Phân trang + tìm kiếm
    @GetMapping
    public Page<AuthorResponse> getAuthors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        if (search != null && !search.trim().isEmpty()) {
            return authorService.searchAuthors(search, page, size);
        }
        return authorService.getAllPaged(page, size);
    }

    @GetMapping(path = "/")
    public ResponseEntity<List<AuthorResponse>> getAllAuthors() {
        return ResponseEntity.ok(authorService.findAll());
    }

//    @GetMapping
//    public ResponseEntity<List<AuthorResponse>> getAll() {
//        return ResponseEntity.ok(authorService.findAll());
//    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AuthorResponse> create(@RequestBody AuthorRequest request) {
        return ResponseEntity.ok(authorService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponse> update(@PathVariable Long id, @RequestBody AuthorRequest request) {
        return ResponseEntity.ok(authorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
