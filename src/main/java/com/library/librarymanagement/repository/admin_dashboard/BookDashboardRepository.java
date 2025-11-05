package com.library.librarymanagement.repository.admin_dashboard;

import com.library.librarymanagement.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookDashboardRepository extends JpaRepository<Book, Long> {

    // Đếm số sách chưa bị xóa mềm
    long countByIsDeletedFalse();

    // Nếu entity Book KHÔNG có field isDeleted
    // thì bạn bỏ dòng trên, dùng luôn: long count();
}
