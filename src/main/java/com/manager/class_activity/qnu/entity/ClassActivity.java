package com.manager.class_activity.qnu.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "class_activity")
public class ClassActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    Class clazz;  //done

    @ManyToOne
    @JoinColumn(name = "leader_id", nullable = false)
    Student leader;  //done

    @ManyToOne
    @JoinColumn(name = "activity_guide_id", nullable = false)
    ActivityGuide activityGuide; //done

    @Column(name = "activity_time", nullable = false)
    Timestamp activityTime;

    @Column(name = "created_at", nullable = false)
    Timestamp createdAt;

    @Column(name = "updated_at", nullable = false)
    Timestamp updatedAt;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    boolean isDeleted;

    @OneToMany(mappedBy = "classActivity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    Set<ActivityView> activityViews;

    @OneToMany(mappedBy = "classActivity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    Set<AttendanceSession> attendanceSessions;

    @OneToMany(mappedBy = "classActivity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    Set<Notification> notifications;

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
