package com.library.librarymanagement.service.book;


import com.library.librarymanagement.dto.request.BookRequest;
import com.library.librarymanagement.dto.response.BookResponse;
import com.library.librarymanagement.entity.Author;
import com.library.librarymanagement.entity.Book;
import com.library.librarymanagement.entity.Category;
import com.library.librarymanagement.entity.Publisher;
import com.library.librarymanagement.repository.CategoryRepository;
import com.library.librarymanagement.repository.author.AuthorRepository;
import com.library.librarymanagement.repository.publisher.PublisherRepository;
import com.library.librarymanagement.repository.user.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {

    private String uploadDir = "uploads/books";;

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final CategoryRepository categoryRepository;

    public List<BookResponse> getAllBooks() {
        List<BookResponse> bookResponses = new ArrayList<>();
        List<Book> books = bookRepository.findAllByIsDeletedFalse();
        for (Book book : books) {
            System.out.println("Author: " + book.getAuthor().getFullName());
            BookResponse bookResponse = new BookResponse();
            bookResponse.setId(book.getId().intValue());
            bookResponse.setTitle(book.getTitle());
            bookResponse.setAuthor(book.getAuthor().getFullName());
            bookResponse.setImage(book.getImage());
            bookResponse.setIsDeleted(String.valueOf(book.getIsDeleted()));
            bookResponse.setCategory(book.getCategory().getName());
            bookResponse.setPublisher(book.getPublisher().getCompanyName());
            bookResponse.setPublishedDate(book.getPublishedDate());
            bookResponse.setImportedDate(book.getImportedDate());
            bookResponses.add(bookResponse);
        }
        return bookResponses;
    }

    public List<BookResponse> getAllAdminBooks() {
        List<BookResponse> bookResponses = new ArrayList<>();
        List<Book> books = bookRepository.findAll();
        for (Book book : books) {
            System.out.println("Author: " + book.getAuthor().getFullName());
            BookResponse bookResponse = new BookResponse();
            bookResponse.setId(book.getId().intValue());
            bookResponse.setTitle(book.getTitle());
            bookResponse.setAuthor(book.getAuthor().getFullName());
            bookResponse.setImage(book.getImage());
            bookResponse.setIsDeleted(String.valueOf(book.getIsDeleted()));
            bookResponse.setCategory(book.getCategory().getName());
            bookResponse.setPublisher(book.getPublisher().getCompanyName());
            bookResponse.setPublishedDate(book.getPublishedDate());
            bookResponse.setImportedDate(book.getImportedDate());
            bookResponses.add(bookResponse);
        }
        return bookResponses;
    }

    public Book createBook(BookRequest req) throws IOException {

        Author author = authorRepository.findById(req.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found"));
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Publisher publisher = publisherRepository.findById(req.getPublisherId())
                .orElseThrow(() -> new RuntimeException("Publisher not found"));

        // 🔹 Xử lý upload ảnh
        String imageUrl = null;
        if (req.getImage() != null && !req.getImage().isEmpty()) {
            // Tạo thư mục nếu chưa có
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Tạo tên file ngẫu nhiên tránh trùng
            String fileName = UUID.randomUUID() + "_" + req.getImage().getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // Lưu file
            Files.copy(req.getImage().getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            imageUrl = "/uploads/books/" + fileName; // 🔹 đường dẫn để frontend hiển thị
        }

        Book book = new Book();
        book.setBookCode(req.getBookCode());
        book.setDescription(req.getDescription());
        book.setTitle(req.getTitle());
        book.setAuthor(author);
        book.setCategory(category);
        book.setPublisher(publisher);
        book.setImage(imageUrl);

        return bookRepository.save(book);
    }
}
