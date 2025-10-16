package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "Notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private Timestamp createdDate;

    @ManyToOne
    @JoinColumn(name = "to_user")
    private Account toUser;
}
