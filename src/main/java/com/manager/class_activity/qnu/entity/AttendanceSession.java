package com.manager.class_activity.qnu.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "attendance_session")
public class AttendanceSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "class_activity_id", nullable = false)
    ClassActivity classActivity; //done

    @Column(name = "attendance_code", nullable = false)
    String attendanceCode;

    @Column(name = "start_time", nullable = false)
    Timestamp startTime;

    @Column(name = "end_time", nullable = false)
    Timestamp endTime;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    Timestamp createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    Timestamp updatedAt;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    boolean isDeleted;

    @OneToMany(mappedBy = "attendanceSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<AttendanceRecord> attendanceRecords;

    @PrePersist
    protected void onCreate() {
        createdAt = Timestamp.valueOf(LocalDateTime.now());
        updatedAt = Timestamp.valueOf(LocalDateTime.now());
        startTime = Timestamp.valueOf(LocalDateTime.now());
        endTime = Timestamp.valueOf(LocalDateTime.now().plusMinutes(45));
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Timestamp.valueOf(LocalDateTime.now());
    }
}
