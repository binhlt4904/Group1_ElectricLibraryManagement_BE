package com.library.librarymanagement.controller.author;

import com.library.librarymanagement.dto.response.AuthorResponse;
import com.library.librarymanagement.service.author.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/authors")
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping(path = "/")
    public ResponseEntity<List<AuthorResponse>> getAllAuthors() {
        return ResponseEntity.ok(authorService.findAll());
    }
}
