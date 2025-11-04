package com.library.librarymanagement.repository.admin_dashboard;

import com.library.librarymanagement.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountDashboardRepository extends JpaRepository<Account, Long> {

    // Đếm số account có role.id = 3 và status = ACTIVE
    long countByRole_IdAndStatus(Long roleId, String status);

    // Nếu trong entity Account bạn dùng field "roleId" (int/long) chứ không phải "role"
    // thì sửa method trên thành:
    // long countByRoleIdAndStatus(Long roleId, AccountStatus status);
}
