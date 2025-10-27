package com.library.librarymanagement.repository;

import com.library.librarymanagement.entity.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemUserRepository extends JpaRepository<SystemUser, Long> {
    boolean existsByAccountId(Long accountId);
    Optional<SystemUser> findByAccountId(Long accountId);
}
