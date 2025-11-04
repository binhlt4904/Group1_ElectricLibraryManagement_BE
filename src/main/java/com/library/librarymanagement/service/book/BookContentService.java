package com.library.librarymanagement.service.book;

import com.library.librarymanagement.entity.Book;
import com.library.librarymanagement.entity.BookContent;
import com.library.librarymanagement.repository.user.BookContentRepository;
import com.library.librarymanagement.repository.user.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookContentService {
    private final BookRepository bookRepository;
    private final BookContentRepository bookContentRepository;

    private String uploadDir = "uploads";

    public BookContent create(Long bookId, String title, Integer chapter, MultipartFile file) throws IOException {
        // 1) Validate cơ bản
        if (bookId == null) throw new IllegalArgumentException("bookId is required");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title is required");
        if (chapter == null) throw new IllegalArgumentException("chapter is required");
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("file is required");

        // 2) Validate PDF
        String contentType = file.getContentType();
        String originalName = Objects.requireNonNullElse(file.getOriginalFilename(), "");
        boolean isPdfByMime = MediaType.APPLICATION_PDF_VALUE.equalsIgnoreCase(contentType);
        boolean isPdfByName = originalName.toLowerCase().endsWith(".pdf");
        if (!isPdfByMime && !isPdfByName) {
            throw new IllegalArgumentException("File must be a PDF");
        }

        // 3) Tìm book
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));

        // 4) Tạo thư mục theo bookId
        Path baseDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path bookDir = baseDir.resolve("books").resolve(String.valueOf(bookId)).resolve("contents");
        Files.createDirectories(bookDir);

        // 5) Đặt tên file an toàn
        String safeName = sanitizeFilename(originalName);
        if (!safeName.toLowerCase().endsWith(".pdf")) safeName += ".pdf";
        String finalName = UUID.randomUUID() + "-" + safeName;
        Path target = bookDir.resolve(finalName);

        // 6) Ghi file
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // 7) Lưu DB (lưu đường dẫn tương đối để dễ move server)
        String relativePath = Paths.get("uploads", "books", String.valueOf(bookId), "contents", finalName)
                .toString().replace("\\", "/");

        BookContent bookContent = new BookContent();
        bookContent.setTitle(title);
        bookContent.setChapter(chapter);
        bookContent.setBook(book);
        bookContent.setContent("/" + relativePath);

        return bookContentRepository.save(bookContent);
    }

    private String sanitizeFilename(String name) {
        // bỏ ký tự lạ
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    public BookContent getBookContent(Long bookId, int chapter) {
        return bookContentRepository.findBookContentByBookIdAndChapter(bookId, chapter)
                .orElseThrow(() -> new IllegalArgumentException("Book content not found for bookId " + bookId + " and chapter " + chapter));
    }
}
