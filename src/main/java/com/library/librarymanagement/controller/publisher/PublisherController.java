package com.library.librarymanagement.controller.publisher;

import com.library.librarymanagement.dto.request.PublisherRequest;
import com.library.librarymanagement.dto.response.PublisherResponse;
import com.library.librarymanagement.repository.publisher.PublisherRepository;
import com.library.librarymanagement.service.publisher.PublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/publishers")
public class PublisherController {

    private final PublisherService publisherService;

    // 🔹 Phân trang + tìm kiếm
    @GetMapping
    public Page<PublisherResponse> getPublishers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search
    ) {
        if (search != null && !search.trim().isEmpty()) {
            return publisherService.searchPublishers(search, page, size);
        }
        return publisherService.getAllPaged(page, size);
    }

    @GetMapping("/")
    public ResponseEntity<List<PublisherResponse>> getPublisher( ){
        return ResponseEntity.ok(publisherService.getAllPublishers());
    }



    /** 🔹 Lấy theo ID */
    @GetMapping("/{id}")
    public ResponseEntity<PublisherResponse> getPublisherById(@PathVariable Long id) {
        return ResponseEntity.ok(publisherService.getPublisherById(id));
    }

    /** 🔹 Tạo mới */
    @PostMapping
    public ResponseEntity<PublisherResponse> createPublisher(@RequestBody PublisherRequest request) {
        return ResponseEntity.ok(publisherService.createPublisher(request));
    }

    /** 🔹 Cập nhật */
    @PutMapping("/{id}")
    public ResponseEntity<PublisherResponse> updatePublisher(@PathVariable Long id,
                                                             @RequestBody PublisherRequest request) {
        return ResponseEntity.ok(publisherService.updatePublisher(id, request));
    }

    /** 🔹 Xóa mềm */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable Long id) {
        publisherService.deletePublisher(id);
        return ResponseEntity.noContent().build();
    }
}
