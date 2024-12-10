package com.manager.class_activity.qnu.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityGuideResponse {
    int id;
    String name;
    String pdfUrl;
    Timestamp createdAt;
    Timestamp updatedAt;
}
