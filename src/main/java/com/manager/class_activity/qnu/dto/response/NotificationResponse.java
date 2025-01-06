package com.manager.class_activity.qnu.dto.response;

import com.manager.class_activity.qnu.entity.NotificationRecipientEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    String message;
    int classActivityId;
    Timestamp notificationTime;
    NotificationRecipientEnum notificationRecipientEnum;
    boolean isRead;
}
