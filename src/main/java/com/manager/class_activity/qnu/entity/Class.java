package com.manager.class_activity.qnu.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "class")
public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(nullable = false)
    String name;

    @ManyToOne
    @JoinColumn(name = "department_id")
    Department department; //done

    @ManyToOne
    @JoinColumn(name = "course_id")
    Course course; //done

    @Column(name = "created_at", nullable = false)
    Timestamp createdAt;

    @Column(name = "updated_at", nullable = false)
    Timestamp updatedAt;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    boolean isDeleted;

    @OneToMany(mappedBy = "clazz", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    Set<AcademicAdvisor> academicAdvisors;

    @OneToMany(mappedBy = "clazz", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    Set<ClassActivity> classActivities;

    @OneToMany(mappedBy = "clazz", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Where(clause = "is_deleted = false")
    Set<Student> students;

    @OneToMany(mappedBy = "clazz", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    Set<StudentPosition> studentPositions;

    @Column(name = "duration_years", nullable = false, precision = 3, scale = 1, columnDefinition = "DECIMAL(3,1) DEFAULT 4.0")
    BigDecimal durationYears = BigDecimal.valueOf(4.0);

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

