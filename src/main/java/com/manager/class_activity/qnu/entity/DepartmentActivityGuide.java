package com.manager.class_activity.qnu.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "department_activity_guide")
public class DepartmentActivityGuide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

//    @ManyToOne
//    @JoinColumn(name = "activity_guide_id", nullable = false)
//    ActivityGuide activityGuide; //done
    @Column(name = "name")
    String name;

    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    Activity activity; //done

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    Department department; //done

    @Column(name = "pdf_url")
    String pdfUrl;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    Timestamp createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
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
