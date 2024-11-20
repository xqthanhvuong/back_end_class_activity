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
@Builder
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    Status status;

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
        if (status == null) {
            status = Status.PLANNED; // Giá trị mặc định
        }
        createdAt = Timestamp.valueOf(LocalDateTime.now());
        updatedAt = Timestamp.valueOf(LocalDateTime.now());
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Timestamp.valueOf(LocalDateTime.now());
    }
}
