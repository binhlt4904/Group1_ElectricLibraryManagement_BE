package com.library.librarymanagement.repository.account;

import com.library.librarymanagement.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.Collection;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUsername(String username);
    Optional<Account> findByPhone(String phone);
    Optional<Account> findByEmail(String email);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String email);

    List<Account> findAllByRole_NameIn(Collection<String> roleNames);

    @Query("""
           SELECT a FROM Account a
           WHERE a.role.name IN :roleNames
           """)
    List<Account> findAccountsByRoleNames(@Param("roleNames") Collection<String> roleNames);

    @Query("""
           SELECT a
           FROM Account a
           JOIN a.role r
           WHERE r.id IN (2, 3)
             AND (:fullName IS NULL OR LOWER(a.fullName) LIKE LOWER(CONCAT('%', :fullName, '%')))
             AND (:status   IS NULL OR UPPER(a.status) = UPPER(:status))
             AND (:roleId   IS NULL OR r.id = :roleId)
           """)
    Page<Account> getAllAccounts(
            @Param("fullName") String fullName,
            @Param("status")   String status,
            @Param("roleId")   Long roleId,
            Pageable pageable
    );
    // dùng cho update: kiểm tra trùng nhưng loại trừ chính record đang update
    boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
