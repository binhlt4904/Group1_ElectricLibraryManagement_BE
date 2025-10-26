package com.library.librarymanagement.service.book;


import com.library.librarymanagement.dto.response.BookResponse;
import com.library.librarymanagement.entity.Book;
import com.library.librarymanagement.repository.user.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    public List<BookResponse> getAllBooks() {
        List<BookResponse> bookResponses = new ArrayList<>();
        List<Book> books = bookRepository.findAllByIsDeletedFalse();
        for (Book book : books) {
            BookResponse bookResponse = new BookResponse();
            bookResponse.setId(book.getId().intValue());
            bookResponse.setTitle(book.getTitle());
            bookResponse.setAuthor(book.getAuthor().getFullName());
            bookResponse.setImage(book.getImage());
            bookResponse.setIsDeleted(String.valueOf(book.getIsDeleted()));
            bookResponse.setCategory(book.getCategory().getName());
            bookResponse.setPublishedDate(book.getPublishedDate());
            bookResponses.add(bookResponse);
        }
        return bookResponses;
    }
}
