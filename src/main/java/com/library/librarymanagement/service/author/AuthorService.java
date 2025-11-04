package com.library.librarymanagement.service.author;

import com.library.librarymanagement.dto.response.AuthorResponse;
import com.library.librarymanagement.entity.Author;
import com.library.librarymanagement.repository.author.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository authorRepository;

    public List<AuthorResponse> findAll() {
        return authorRepository.findAll().stream().map(author -> {;
            AuthorResponse authorResponse = new AuthorResponse();
            authorResponse.setId(author.getId());
            authorResponse.setFullName(author.getFullName());
            authorResponse.setBiography(author.getBiography());
            authorResponse.setEmail(author.getEmail());
            return authorResponse;
        }).toList();
    }
}
