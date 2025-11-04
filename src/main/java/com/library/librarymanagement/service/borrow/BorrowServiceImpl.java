package com.library.librarymanagement.service.borrow;

import com.library.librarymanagement.dto.response.BorrowRecordResponse;
import com.library.librarymanagement.entity.Book;
import com.library.librarymanagement.entity.BorrowRecord;
import com.library.librarymanagement.entity.LibraryCard;
import com.library.librarymanagement.entity.Reader;
import com.library.librarymanagement.exception.ObjectNotExistException;
import com.library.librarymanagement.repository.borrow.BorrowRepository;
import com.library.librarymanagement.repository.ReaderRepository;
import com.library.librarymanagement.repository.borrow.LibraryCardRepository;
import com.library.librarymanagement.repository.user.BookRepository;
import com.library.librarymanagement.security.JwtService;
import com.library.librarymanagement.service.custom_user_details.CustomUserDetails;
import com.library.librarymanagement.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;
    private final LibraryCardRepository libraryCardRepository;
    private final JwtService jwtService;

    @Override
    @Transactional
    public void borrowBook(Long bookId, Date allowDate) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long accountId;
        if (principal instanceof CustomUserDetails userDetails) {
            accountId = userDetails.getAccountId();
        } else {
            throw new RuntimeException("Cannot extract accountId: invalid principal");
        }

        Reader reader = readerRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ObjectNotExistException("Reader is not found" + accountId));
        Long readerId = reader.getId();

        LibraryCard card = libraryCardRepository.findByReader_Id(readerId)
                .orElseThrow(() -> new ObjectNotExistException("LibraryCard is not found" + readerId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ObjectNotExistException("Book is not found" + bookId));

        if (borrowRepository.findActiveBorrowRecordByLibraryCardAndBook(card.getCardNumber(), book.getId()).isPresent()){
            throw new ObjectNotExistException("Book is already borrowed and need to return");
        }
        Timestamp borrowDate = new Timestamp(System.currentTimeMillis());
        String borrowToken = jwtService.generateBorrowToken(card.getCardNumber(), book.getId(), borrowDate);
        BorrowRecord borrowRecord = BorrowRecord.builder()
                .book(book)
                .borrowedDate(borrowDate)
                .status("Borrowed")
                .allowedDate(allowDate)
                .createdBy(accountId)
                .libraryCard(card)
                .accessToken(borrowToken)
                .build();
        try {
            borrowRepository.save(borrowRecord);
        } catch (Exception e){
            throw new RuntimeException("Fail to save database" + bookId);
        }
    }

    @Override
    @Transactional
    public Page<BorrowRecordResponse> searchBorrowRecords(String search, String status, Date fromDate, Date toDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<BorrowRecord> specification = Specification.allOf();

        if (search != null && !search.isBlank()) {
            specification = specification
                    .and((root, query, cb) -> cb.or(
                            cb.like(cb.lower(root.join("book").get("title")), "%" + search.toLowerCase() + "%"),
                            cb.like(cb.lower(root.join("book").join("author").get("fullName")), "%" + search.toLowerCase() + "%"),
                            cb.like(cb.lower(root.join("libraryCard").join("reader").join("account").get("fullName")),"%" + search.toLowerCase() + "%")
                    ));
        }

        if (status != null && !status.isBlank()) {
            specification = specification.and((root, query, cb) ->
                    cb.equal(root.get("status"), status)
            );
        }

        if (fromDate != null){
            specification = specification.and((root, query, cb) ->
                      cb.greaterThanOrEqualTo(root.get("borrowedDate"), fromDate)
            );
        }

        if (toDate != null){
            specification = specification.and((root, query, cb) ->
                        cb.lessThanOrEqualTo(root.get("borrowedDate"), toDate)
            );
        }
        Page<BorrowRecord> result = borrowRepository.findAll(specification, pageable);
        return result.map((record) -> {
            return Mapper.mapEntityToDTO(record);
        });
    }

    @Override
    public List<BorrowRecordResponse> searchBorrowRecordsStatistic(Date fromDate, Date toDate) {
        Specification<BorrowRecord> specification = Specification.allOf();
        if (fromDate != null){
            specification = specification.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("borrowedDate"), fromDate)
            );
        }

        if (toDate != null){
            specification = specification.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("borrowedDate"), toDate)
            );
        }
        List<BorrowRecord> result = borrowRepository.findAll(specification);
        return result.stream().map((record) -> {
            return Mapper.mapEntityToDTO(record);
        }).collect(Collectors.toList());
    }

    @Override
    public Page<BorrowRecordResponse> searchBorrowRecordsBySpecReader(String search, String status, Date fromDate, Date toDate, int page, int size) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long accountId;
        if (principal instanceof CustomUserDetails userDetails) {
            accountId = userDetails.getAccountId();
        } else {
            throw new RuntimeException("Cannot extract accountId: invalid principal");
        }

        Reader reader = readerRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ObjectNotExistException("Reader is not found" + accountId));
        Long readerId = reader.getId();

        LibraryCard card = libraryCardRepository.findByReader_Id(readerId)
                .orElseThrow(() -> new ObjectNotExistException("LibraryCard is not found" + readerId));

        Pageable pageable = PageRequest.of(page, size);
        Specification<BorrowRecord> specification = Specification.allOf();

        specification = specification.and((root, query, cb) ->
                cb.equal(root.get("libraryCard").get("id"), card.getId()));

        if (search != null && !search.isBlank()) {
            specification = specification
                    .and((root, query, cb) -> cb.or(
                            cb.like(cb.lower(root.join("book").get("title")), "%" + search.toLowerCase() + "%"),
                            cb.like(cb.lower(root.join("book").join("author").get("fullName")), "%" + search.toLowerCase() + "%")
                    ));
        }

        if (status != null && !status.isBlank()) {
            specification = specification.and((root, query, cb) ->
                    cb.equal(root.get("status"), status)
            );
        }

        if (fromDate != null){
            specification = specification.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("borrowedDate"), fromDate)
            );
        }

        if (toDate != null){
            specification = specification.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("borrowedDate"), toDate)
            );
        }
        Page<BorrowRecord> result = borrowRepository.findAll(specification, pageable);
        return result.map((record) -> {
            return Mapper.mapEntityToDTO(record);
        });
    }

    @Override
    public List<BorrowRecordResponse> searchBorrowRecordsStatisticBySpecReader(Date fromDate, Date toDate) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long accountId;
        if (principal instanceof CustomUserDetails userDetails) {
            accountId = userDetails.getAccountId();
        } else {
            throw new RuntimeException("Cannot extract accountId: invalid principal");
        }

        Reader reader = readerRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ObjectNotExistException("Reader is not found" + accountId));
        Long readerId = reader.getId();

        LibraryCard card = libraryCardRepository.findByReader_Id(readerId)
                .orElseThrow(() -> new ObjectNotExistException("LibraryCard is not found" + readerId));

        Specification<BorrowRecord> specification = Specification.allOf();

        specification = specification.and((root, query, cb) ->
                cb.equal(root.get("libraryCard").get("id"), card.getId()));

        if (fromDate != null){
            specification = specification.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("borrowedDate"), fromDate)
            );
        }

        if (toDate != null){
            specification = specification.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("borrowedDate"), toDate)
            );
        }
        List<BorrowRecord> result = borrowRepository.findAll(specification);
        return result.stream().map((record) -> {
            return Mapper.mapEntityToDTO(record);
        }).collect(Collectors.toList());
    }
}
