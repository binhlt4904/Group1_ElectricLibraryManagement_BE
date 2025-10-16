package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "description")
    private String description;

    @Column(name = "title")
    private String title;

    // Quan hệ: nhiều event thuộc 1 account
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user", referencedColumnName = "id") // foreign key đến account.id
    private Account account;

}
