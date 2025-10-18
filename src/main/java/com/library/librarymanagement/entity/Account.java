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

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Event> events;

    @Column(name = "phone", length = 20)
    private String phone;

    // 1-1 với Reader (FK ở bảng reader.account_id)
    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY)
    private Reader reader;

    // 1-1 với SystemUser (FK ở bảng system_user.account_id)
    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY)
    private SystemUser systemUser;

    // 1-n với Role (FK ở role.account_id)
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    private Set<Role> roles;
}
