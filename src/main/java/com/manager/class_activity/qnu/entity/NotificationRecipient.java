package com.manager.class_activity.qnu.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "notification_recipient")
public class NotificationRecipient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "notification_id", nullable = false)
    Notification notification;  //done

    @Enumerated(EnumType.STRING)
    @Column(name = "recipient_type", nullable = false)
    NotificationRecipientEnum recipientType;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    Account account; //done

    @Column(name = "is_read", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    boolean isRead;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    Timestamp createdAt;
}
