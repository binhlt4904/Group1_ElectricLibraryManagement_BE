package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "Account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String status;
    private String fullName;
    private String email;
    private String phone;

    @OneToMany(mappedBy = "account")
    private Set<Reader> readers;

    @OneToMany(mappedBy = "account")
    private Set<SystemUser> systemUsers;

    @ManyToMany
    @JoinTable(
            name = "Role_Account",
            joinColumns = @JoinColumn(name = "AccountId"),
            inverseJoinColumns = @JoinColumn(name = "RoleName")
    )
    private Set<Role> roles;
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Event> events;

}
