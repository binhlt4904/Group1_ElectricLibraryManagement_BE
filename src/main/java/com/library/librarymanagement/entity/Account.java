package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, length = 255, unique = true)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "status", length = 255)
    private String status;

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(name = "email", length = 255, unique = true)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    // Một account có thể liên kết nhiều Reader
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private Set<Reader> readers;

    // Một account có thể liên kết nhiều SystemUser
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private Set<SystemUser> systemUsers;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Event> events;

    // Một account có thể xác nhận nhiều Report
    @OneToMany(mappedBy = "confirmedBy", fetch = FetchType.LAZY)
    private Set<Report> confirmedReports;

    // ✅ Quan hệ N-N với Role qua bảng Role_Account
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_account",
            joinColumns = @JoinColumn(name = "AccountId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "RoleName", referencedColumnName = "name")
    )
    private Set<Role> roles;
}
