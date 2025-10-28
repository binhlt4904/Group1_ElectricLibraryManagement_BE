package com.library.librarymanagement.service.book;


import com.library.librarymanagement.dto.request.BookRequest;
import com.library.librarymanagement.dto.response.BookContentResponse;
import com.library.librarymanagement.dto.response.BookResponse;
import com.library.librarymanagement.entity.*;
import com.library.librarymanagement.repository.CategoryRepository;
import com.library.librarymanagement.repository.author.AuthorRepository;
import com.library.librarymanagement.repository.publisher.PublisherRepository;
import com.library.librarymanagement.repository.user.BookContentRepository;
import com.library.librarymanagement.repository.user.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private final BookContentRepository bookContentRepository;

    public List<BookResponse> getAllBooks() {
        List<BookResponse> bookResponses = new ArrayList<>();
        List<Book> books = bookRepository.findAllByIsDeletedFalse();
        for (Book book : books) {
            System.out.println("Author: " + book.getAuthor().getFullName());
            BookResponse bookResponse = convert(book);
            bookResponses.add(bookResponse);
        }
        return bookResponses;
    }

    public BookResponse getBookById(Long id) {
        Book book =  bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        BookResponse bookResponse = convert(book);
        return bookResponse;

    }

    public List<BookResponse> getAllAdminBooks() {
        List<BookResponse> bookResponses = new ArrayList<>();
        List<Book> books = bookRepository.findAll();
        for (Book book : books) {
            BookResponse response= convert(book);
            bookResponses.add(response);
        }
        return bookResponses;
    }

    public Book createBook(BookRequest req) throws IOException {
        System.out.println(req.getPublishedDate());

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
        book.setImportedDate(new Date());
        book.setIsDeleted(false);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            book.setPublishedDate(sdf.parse(req.getPublishedDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        book.setImage(imageUrl);

        return bookRepository.save(book);
    }

    public List<BookContentResponse> getBookContent(Long id) {
        List<BookContent> bookContents = bookContentRepository.findByBookId(id);
        List<BookContentResponse> responses = new ArrayList<>();
        for (BookContent bookContent : bookContents) {
            BookContentResponse response = new BookContentResponse();
            response.setId(bookContent.getId());
            response.setChapter(String.valueOf(bookContent.getChapter()));
            response.setContent(bookContent.getContent());
            response.setTitle(bookContent.getTitle());
            responses.add(response);
        }


        return responses;
    }

    public List<BookContentResponse> getBookContentUser(Long id) {
        List<BookContentResponse> responses = getBookContent(id);
        for (BookContentResponse response : responses) {
            response.setContent(null);
        }


        return responses;
    }





    public BookResponse convert(Book book){
        BookResponse bookResponse = new BookResponse();
        bookResponse.setId(book.getId().intValue());
        bookResponse.setBookCode(book.getBookCode());
        bookResponse.setDescription(book.getDescription());
        bookResponse.setTitle(book.getTitle());
        bookResponse.setAuthor(book.getAuthor().getFullName());
        bookResponse.setImage(book.getImage());
        bookResponse.setIsDeleted(String.valueOf(book.getIsDeleted()));
        bookResponse.setCategory(book.getCategory().getName());
        bookResponse.setPublisher(book.getPublisher().getCompanyName());
        bookResponse.setPublishedDate(book.getPublishedDate());
        bookResponse.setImportedDate(book.getImportedDate());
        return bookResponse;
    }

    public Page<BookResponse> getBooks(Pageable pageable, String search, String category, String status) {
        Specification<Book> spec = Specification.allOf();
        if(search != null && !search.isEmpty()) {
            spec = spec.and((root,query,cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("author").get("fullName")), "%" + search.toLowerCase() + "%"),
                            cb.like(cb.lower(root.get("title")),"%" + search.toLowerCase() + "%")
                    ));
        }
        if(category != null && !category.isEmpty()) {
            spec = spec.and((root,query,cb) ->
                    cb.equal(root.get("category").get("name"), category)
            );
        }

        if(status != null && !status.isEmpty()) {
            Boolean isDeleted = Boolean.parseBoolean(status);
            spec = spec.and((root,query,cb) ->
                    cb.equal(root.get("isDeleted"), isDeleted)
            );
        }
        return bookRepository.findAll(spec, pageable).map(this::convert);

    }
}
