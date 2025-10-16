package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "Role")
public class Role {
    @Id
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<Account> accounts;
}
