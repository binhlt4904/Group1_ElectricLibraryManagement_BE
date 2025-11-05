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

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));

        String relativePath= saveBookContentFile(book, file);


        BookContent bookContent = new BookContent();
        bookContent.setTitle(title);
        bookContent.setChapter(chapter);
        bookContent.setBook(book);
        bookContent.setIsDeleted(false);
        bookContent.setContent(relativePath);

        return bookContentRepository.save(bookContent);
    }

    public BookContent update(Long contentId, String title, Integer chapter, MultipartFile file) throws IOException {

        BookContent existing = bookContentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Book content not found: " + contentId));

        Book book = existing.getBook();

        if (title != null && !title.isBlank()) {
            existing.setTitle(title);
        }
        if (chapter != null) {
            existing.setChapter(chapter);
        }

        if (file != null && !file.isEmpty()) {
           String relativePath= saveBookContentFile(book, file);

            deleteOldFile(existing.getContent());

            existing.setContent(relativePath);
        }

        return bookContentRepository.save(existing);
    }


    private String sanitizeFilename(String name) {
        // bỏ ký tự lạ
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    public BookContent getBookContent(Long bookId, int chapter) {
        return bookContentRepository.findBookContentByBookIdAndChapter(bookId, chapter)
                .orElseThrow(() -> new IllegalArgumentException("Book content not found for bookId " + bookId + " and chapter " + chapter));
    }

    private void deleteOldFile(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return;
        try {
            Path baseDir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path oldFile = baseDir.resolve(relativePath.replaceFirst("^/uploads/", ""));
            Files.deleteIfExists(oldFile);
        } catch (Exception ex) {
            System.err.println("⚠️ Could not delete old file: " + ex.getMessage());
        }
    }

    private String saveBookContentFile(Book book, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        // Lấy thông tin cơ bản
        String originalName = Objects.requireNonNullElse(file.getOriginalFilename(), "");
        String contentType = file.getContentType();

        // ✅ Kiểm tra file PDF
        boolean isPdfByMime = MediaType.APPLICATION_PDF_VALUE.equalsIgnoreCase(contentType);
        boolean isPdfByName = originalName.toLowerCase().endsWith(".pdf");
        if (!isPdfByMime && !isPdfByName) {
            throw new IllegalArgumentException("File must be a PDF");
        }

        // ✅ Tạo thư mục lưu file
        Path baseDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path bookDir = baseDir.resolve("books")
                .resolve(String.valueOf(book.getId()))
                .resolve("contents");
        Files.createDirectories(bookDir);

        // ✅ Tạo tên file an toàn & duy nhất
        String safeName = sanitizeFilename(originalName);
        if (!safeName.toLowerCase().endsWith(".pdf")) safeName += ".pdf";
        String finalName = UUID.randomUUID() + "-" + safeName;
        Path target = bookDir.resolve(finalName);

        // ✅ Ghi file ra ổ đĩa
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // ✅ Tạo đường dẫn tương đối để lưu DB
        String relativePath = Paths.get("uploads", "books", String.valueOf(book.getId()), "contents", finalName)
                .toString()
                .replace("\\", "/");

        return "/" + relativePath; // => /uploads/books/5/contents/abc.pdf
    }

}
