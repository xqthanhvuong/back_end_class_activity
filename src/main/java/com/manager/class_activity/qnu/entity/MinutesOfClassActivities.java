package com.manager.class_activity.qnu.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "Minutes_of_class_activities")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MinutesOfClassActivities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "last_month_activity", columnDefinition = "TEXT")
    String lastMonthActivity;

    @Column(name = "this_month_activity", columnDefinition = "TEXT")
    String thisMonthActivity;

    @Column(name = "teacher_feedback", columnDefinition = "TEXT")
    String teacherFeedback;

    @Column(name = "class_feedback", columnDefinition = "TEXT")
    String classFeedback;

    @ManyToOne
    @JoinColumn(name = "class_activity_id", referencedColumnName = "id")
    ClassActivity classActivity;

    @Column(name = "created_at", nullable = false)
    Timestamp createdAt;

    @Column(name = "updated_at", nullable = false)
    Timestamp updatedAt;

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
