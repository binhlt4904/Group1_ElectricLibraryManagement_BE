package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Entity
@Table(name = "event")
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "event_date")
    private LocalDate eventDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "location", length = 500)
    private String location;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "registered")
    private Integer registered = 0;

    @Column(name = "status", length = 50)
    private String status; // UPCOMING, ONGOING, COMPLETED, CANCELLED

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private java.util.Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private java.util.Date updatedDate;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user", referencedColumnName = "id", nullable = false)
    private SystemUser fromUser;
}
