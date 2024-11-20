package com.manager.class_activity.qnu.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "academic_advisor")
@FieldDefaults(level = AccessLevel.PRIVATE)
    public class AcademicAdvisor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "lecturer_id")
    Lecturer lecturer; //done

    @ManyToOne
    @JoinColumn(name = "class_id")
    Class clazz; //done

    @Column(name = "academic_year", nullable = false)
    String academicYear;

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
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Timestamp.valueOf(LocalDateTime.now());
    }
}
