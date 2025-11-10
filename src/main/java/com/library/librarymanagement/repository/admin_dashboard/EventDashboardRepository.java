package com.library.librarymanagement.repository.admin_dashboard;

import com.library.librarymanagement.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventDashboardRepository extends JpaRepository<Event, Long> {

    // Lấy 10 event mới nhất
    List<Event> findTop10ByOrderByCreatedDateDesc();
}
