package com.manager.class_activity.qnu.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "student_position")
@Builder
public class StudentPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    Student student; //done

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    Class clazz; //done

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    StudentPositionEnum position;

    @Column(name = "start_date", nullable = false)
    Date startDate;

    @Column(name = "end_date")
    Date endDate;

    @Column(name = "created_at", nullable = false)
    Timestamp createdAt;

    @Column(name = "updated_at", nullable = false)
    Timestamp updatedAt;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    boolean isDeleted;

    @PrePersist
    protected void onCreate() {
        createdAt = Timestamp.valueOf(LocalDateTime.now());
        updatedAt = Timestamp.valueOf(LocalDateTime.now());
        startDate = Date.valueOf(LocalDate.now());
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Timestamp.valueOf(LocalDateTime.now());
    }
}
