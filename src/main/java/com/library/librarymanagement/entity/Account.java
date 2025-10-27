package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "account")
@Data
@ToString(exclude = {"role","systemUser","reader"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY)
    private Reader reader;

    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY)
    private SystemUser systemUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    private Role role;


}
