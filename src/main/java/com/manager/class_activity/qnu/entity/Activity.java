package com.manager.class_activity.qnu.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "activity")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(nullable = false)
    String name;

    @Column(name = "created_at", nullable = false)
    Timestamp createdAt;

    @Column(name = "updated_at", nullable = false)
    Timestamp updatedAt;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    boolean isDeleted;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<ClassActivity> classActivities;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<DepartmentActivityGuide> departmentActivityGuides;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<ActivityGuide> activityGuides;

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
