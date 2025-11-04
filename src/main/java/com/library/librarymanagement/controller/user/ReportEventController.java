package com.library.librarymanagement.controller.user;

import com.library.librarymanagement.service.book_report.ReportEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/reports/stream")
@RequiredArgsConstructor
public class ReportEventController {
    private final ReportEventPublisher reportEventPublisher;

    @GetMapping
    public SseEmitter streamReportEvents() {
        return reportEventPublisher.subscribe();
    }
}
