package com.library.librarymanagement.service.borrow;

import java.util.Date;

public interface BorrowService {
    void borrowBook(Long bookId, Date allowDate);
}
