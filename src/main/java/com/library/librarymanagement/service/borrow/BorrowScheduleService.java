package com.library.librarymanagement.service.borrow;

import com.library.librarymanagement.entity.BorrowRecord;
import com.library.librarymanagement.repository.borrow.BorrowRepository;
import com.library.librarymanagement.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowScheduleService {
    private final BorrowRepository borrowRepository;
    private final EmailService mailService;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void dailyBorrowCheck() {
        Date today = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startOfTomorrow = cal.getTime();

        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date endOfTomorrow = cal.getTime();

        List<BorrowRecord> dueTomorrow = borrowRepository.findRecordsDueTomorrow(startOfTomorrow, endOfTomorrow);
        if (!dueTomorrow.isEmpty()) {
            System.out.println("📧 Found " + dueTomorrow.size() + " borrow records due tomorrow");
            for (BorrowRecord record : dueTomorrow) {
                String email = record.getLibraryCard()
                        .getReader()
                        .getAccount()
                        .getEmail();

                String bookTitle = record.getBook().getTitle();
                String dueDate = sdf.format(record.getAllowedDate());
                String readerName = record.getLibraryCard()
                        .getReader()
                        .getAccount()
                        .getFullName();

                String subject = "Remind for returning book - " + bookTitle;
                String message = String.format(
                        "Dear %s,\n\nBook \"%s\", you borrowed, will be over due on  %s.\n" +
                                "Please access electric library to return on time.\n\nSincerely,\nFPT Electric Library.",
                        readerName, bookTitle, dueDate
                );

                try {
                    mailService.sendMailToReminder(email, subject, message);
                    System.out.println("📨 Sent reminder to " + email);
                } catch (Exception e) {
                    System.err.println("⚠️ Failed to send mail to " + email + ": " + e.getMessage());
                }
            }
        }

        int updatedCount = borrowRepository.markOverdueRecords(today);

        if (updatedCount > 0) {
            System.out.println("✅ Scheduler: Updated " + updatedCount + " overdue borrow records at " + today);
        } else {
            System.out.println("ℹ️ Scheduler: No overdue borrow records to update today (" + today + ")");
        }
    }
}
