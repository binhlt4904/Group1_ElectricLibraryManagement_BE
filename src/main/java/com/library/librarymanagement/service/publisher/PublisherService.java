package com.library.librarymanagement.service.publisher;

import com.library.librarymanagement.dto.response.PublisherResponse;
import com.library.librarymanagement.entity.Publisher;
import com.library.librarymanagement.repository.publisher.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublisherService {
    private final PublisherRepository publisherRepository;

    public List<PublisherResponse> getAllPublishers() {
        List<Publisher> publishers = publisherRepository.findAll();
       return publishers.stream().map(publisher -> {
            PublisherResponse publisherResponse = new PublisherResponse();
            publisherResponse.setId(publisher.getId());
            publisherResponse.setCompanyName(publisher.getCompanyName());
            publisherResponse.setAddress(publisher.getAddress());
            publisherResponse.setEmail(publisher.getEmail());
            publisherResponse.setPhone(publisher.getPhone());
            publisherResponse.setEstablishedYear(publisher.getEstablishedYear());
            return publisherResponse;
        }).toList();
    }
}
