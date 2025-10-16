package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "description", length = 255)
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private java.util.Date createdDate;

    // Nhiều event thuộc 1 account; cột FK là from_user
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_user", referencedColumnName = "id", nullable = false)
    private Account account;

}
