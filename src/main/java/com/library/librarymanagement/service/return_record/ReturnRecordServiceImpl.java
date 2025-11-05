package com.library.librarymanagement.service.return_record;

import com.library.librarymanagement.dto.response.ReturnRecordResponse;
import com.library.librarymanagement.entity.*;
import com.library.librarymanagement.exception.ObjectNotExistException;
import com.library.librarymanagement.repository.ReaderRepository;
import com.library.librarymanagement.repository.admin.BorrowRecordRepository;
import com.library.librarymanagement.repository.borrow.LibraryCardRepository;
import com.library.librarymanagement.repository.return_record.ReturnRecordRepository;
import com.library.librarymanagement.repository.user.BookRepository;
import com.library.librarymanagement.repository.wallet.WalletRepository;
import com.library.librarymanagement.repository.wallet.WalletTransactionRepository;
import com.library.librarymanagement.service.custom_user_details.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ReturnRecordServiceImpl implements ReturnRecordService {

    private final ReturnRecordRepository returnRecordRepository;
    private final ReaderRepository readerRepository;
    private final LibraryCardRepository libraryCardRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    @Override
    @Transactional
    public ReturnRecordResponse returnRecord(Long borrowId, String note) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Long accountId;
        if (principal instanceof CustomUserDetails userDetails) {
            accountId = userDetails.getAccountId();
        } else {
            throw new RuntimeException("Cannot extract accountId: invalid principal");
        }

        BorrowRecord borrowRecord = borrowRecordRepository.findById(borrowId)
                .orElseThrow(() -> new ObjectNotExistException("Borrow record not found"+ borrowId));

        if ("Returned".equals(borrowRecord.getStatus())) {
            throw new RuntimeException("This book has already been returned");
        }

        Date today = new Date();
        BigDecimal fine = calculateFine(borrowRecord.getAllowedDate(), today);
        ReturnRecord returnRecord = ReturnRecord.builder()
                .borrowRecord(borrowRecord)
                .note(note)
                .fineAmount(fine)
                .status("Returned") // status of return record ?
                .createdBy(accountId)
                .build();

        Reader reader = readerRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ObjectNotExistException("Reader is not found" + accountId));
        Long readerId = reader.getId();
        Wallet wallet = walletRepository.findByReader_Id(readerId);

        if (wallet.getBalance().compareTo(fine) < 0) {
            throw new RuntimeException("Amount of balance is less than fine. Need to deposit");
        }
        wallet.setBalance(wallet.getBalance().subtract(fine)); //TODO: NEED TO SAVE WALLET

        WalletTransaction walletTransaction = WalletTransaction.builder()
                .amount(fine)
                .type("DECREASE") // todo: notice to transactionType that what messsage
                .wallet(wallet)
                .status("Success") // todo: notice to status that what messsage
                .build();
        try {
            ReturnRecord result = returnRecordRepository.save(returnRecord);
            borrowRecord.setReturnRecord(result);
            borrowRecord.setStatus("Returned");
            borrowRecord.setUpdatedBy(accountId); // add with accountId
            borrowRecordRepository.save(borrowRecord);
            walletRepository.save(wallet);
            walletTransactionRepository.save(walletTransaction);
            ReturnRecordResponse returnRecordResponse = ReturnRecordResponse.builder()
                    .bookTitle(borrowRecord.getBook().getTitle())
                    .allowedDate(borrowRecord.getAllowedDate())
                    .returnedDate(result.getReturnedDate())
                    .fine(fine)
                    .status("Book" + borrowRecord.getBook().getTitle() + "is returned successfully")
                    .build();
            return returnRecordResponse;
        } catch (Exception e){
            throw new RuntimeException("Fail to save database about return book transaction with borrowId " + borrowId);
        }
    }

    private BigDecimal calculateFine(Date dueDate, Date returnedDate) {
        long diff = returnedDate.getTime() - dueDate.getTime();
        long daysLate = diff / (1000 * 60 * 60 * 24);

        if (daysLate > 0) {
            return BigDecimal.valueOf(daysLate * 5000); // ví dụ: phạt 5000đ/ngày
        }
        return BigDecimal.ZERO;
    }
}
