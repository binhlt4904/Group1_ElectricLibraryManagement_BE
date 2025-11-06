package com.library.librarymanagement.service.publisher;

import com.library.librarymanagement.dto.request.PublisherRequest;
import com.library.librarymanagement.dto.response.PublisherResponse;
import com.library.librarymanagement.entity.Publisher;
import com.library.librarymanagement.repository.publisher.PublisherRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PublisherService {

    private final PublisherRepository publisherRepository;


    public Page<PublisherResponse> getAllPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Publisher> publisherPage = publisherRepository.findAll(pageable);

        return publisherPage.map(this::mapToResponse);
    }

    public Page<PublisherResponse> searchPublishers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Publisher> publisherPage = publisherRepository.searchPublishers(keyword, pageable);

        return publisherPage.map(this::mapToResponse);
    }


    public List<PublisherResponse> getAllPublishers() {
        return publisherRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }


    public PublisherResponse getPublisherById(Long id) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found"));
        return mapToResponse(publisher);
    }


    public PublisherResponse createPublisher(PublisherRequest request) {
        Publisher publisher = Publisher.builder()
                .companyName(request.getCompanyName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .establishedYear(request.getEstablishedYear())
                .website(request.getWebsite())
                .avatarUrl(request.getAvatarUrl())
                .description(request.getDescription())
                .createdDate(new Timestamp(System.currentTimeMillis()))
                .isDeleted(false)
                .build();

        publisherRepository.save(publisher);
        return mapToResponse(publisher);
    }

    public PublisherResponse updatePublisher(Long id, PublisherRequest request) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found"));

        publisher.setCompanyName(request.getCompanyName());
        publisher.setEmail(request.getEmail());
        publisher.setPhone(request.getPhone());
        publisher.setAddress(request.getAddress());
        publisher.setEstablishedYear(request.getEstablishedYear());
        publisher.setWebsite(request.getWebsite());
        publisher.setAvatarUrl(request.getAvatarUrl());
        publisher.setDescription(request.getDescription());
        publisher.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        if (request.getIsDeleted() != null) {
            publisher.setIsDeleted(request.getIsDeleted());
        }
        publisherRepository.save(publisher);
        return mapToResponse(publisher);
    }

    /**
     * 🔹 Xóa mềm
     */
    public void deletePublisher(Long id) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publisher not found"));
        publisher.setIsDeleted(true);
        publisherRepository.save(publisher);
    }

    /**
     * 🔹 Map Entity → DTO
     */
    private PublisherResponse mapToResponse(Publisher publisher) {
        PublisherResponse response = new PublisherResponse();
        response.setId(publisher.getId());
        response.setCompanyName(publisher.getCompanyName());
        response.setEmail(publisher.getEmail());
        response.setPhone(publisher.getPhone());
        response.setAddress(publisher.getAddress());
        response.setEstablishedYear(publisher.getEstablishedYear());
        response.setWebsite(publisher.getWebsite());
        response.setAvatarUrl(publisher.getAvatarUrl());
        response.setDescription(publisher.getDescription());
        response.setCreatedDate(publisher.getCreatedDate());
        response.setUpdatedDate(publisher.getUpdatedDate());
        response.setIsDeleted(publisher.getIsDeleted());
        return response;
    }
}
