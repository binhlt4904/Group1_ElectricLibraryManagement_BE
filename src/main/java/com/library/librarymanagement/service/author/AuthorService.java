package com.library.librarymanagement.service.author;

import com.library.librarymanagement.dto.request.AuthorRequest;
import com.library.librarymanagement.dto.response.AuthorResponse;
import com.library.librarymanagement.entity.Author;
import com.library.librarymanagement.repository.author.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository authorRepository;

    /** 🔹 Lấy danh sách tất cả tác giả */
    public List<AuthorResponse> findAll() {
        return authorRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }


    /** 🔹 Thêm mới tác giả */
    public AuthorResponse create(AuthorRequest request) {
        Author author = Author.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .biography(request.getBiography())
                .avatarUrl(request.getAvatarUrl())
                .gender(request.getGender())
                .birthDate(request.getBirthDate())
                .deathDate(request.getDeathDate())
                .nationality(request.getNationality())
                .website(request.getWebsite())
                .socialLinks(request.getSocialLinks())
                .createdDate(new Timestamp(System.currentTimeMillis()))
                .isDeleted(false)
                .build();

        authorRepository.save(author);
        return mapToResponse(author);
    }

    /** 🔹 Cập nhật tác giả */
    public AuthorResponse update(Long id, AuthorRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        author.setFullName(request.getFullName());
        author.setEmail(request.getEmail());
        author.setBiography(request.getBiography());
        author.setAvatarUrl(request.getAvatarUrl());
        author.setGender(request.getGender());
        author.setBirthDate(request.getBirthDate());
        author.setDeathDate(request.getDeathDate());
        author.setNationality(request.getNationality());
        author.setWebsite(request.getWebsite());
        author.setSocialLinks(request.getSocialLinks());
        author.setUpdatedDate(new Timestamp(System.currentTimeMillis()));

        authorRepository.save(author);
        return mapToResponse(author);
    }

    public void delete(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        author.setIsDeleted(true);
        authorRepository.save(author);
    }


    /** 🔹 Lấy tác giả theo id */
    public AuthorResponse findById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        return mapToResponse(author);
    }

    /** 🔹 Map entity → DTO */
    private AuthorResponse mapToResponse(Author author) {
        AuthorResponse response = new AuthorResponse();
        response.setId(author.getId());
        response.setFullName(author.getFullName());
        response.setEmail(author.getEmail());
        response.setBiography(author.getBiography());
        response.setAvatarUrl(author.getAvatarUrl());
        response.setGender(author.getGender());
        response.setBirthDate(author.getBirthDate());
        response.setDeathDate(author.getDeathDate());
        response.setNationality(author.getNationality());
        response.setWebsite(author.getWebsite());
        response.setSocialLinks(author.getSocialLinks());
        response.setCreatedDate(author.getCreatedDate());
        response.setUpdatedDate(author.getUpdatedDate());
        response.setIsDeleted(author.getIsDeleted());

        return response;
    }
}
