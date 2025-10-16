package com.library.librarymanagement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Role_Account")
public class RoleAccount {
    @EmbeddedId
    private RoleAccountId id;

    @ManyToOne
    @MapsId("roleName")
    @JoinColumn(name = "RoleName")
    private Role role;

    @ManyToOne
    @MapsId("accountId")
    @JoinColumn(name = "AccountId")
    private Account account;
}
