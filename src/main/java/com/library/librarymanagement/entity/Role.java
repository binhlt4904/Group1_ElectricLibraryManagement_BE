package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "Role")
public class Role {

    @Id
    @Column(name = "name", nullable = false, length = 255, unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<Account> accounts;
}
