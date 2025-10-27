package com.library.librarymanagement.controller.publisher;

import com.library.librarymanagement.dto.response.PublisherResponse;
import com.library.librarymanagement.repository.publisher.PublisherRepository;
import com.library.librarymanagement.service.publisher.PublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/publishers")
public class PublisherController {
    private final PublisherService publisherService;

    @GetMapping(path = "/")
    public ResponseEntity<List<PublisherResponse>> getAllPublishers(){
        return ResponseEntity.ok(publisherService.getAllPublishers());
    }
}
