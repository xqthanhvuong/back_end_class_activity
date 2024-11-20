package com.manager.class_activity.qnu.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassResponse {
    int id;
    String name;
    String departmentName;
    int departmentId;
    String courseName;
    int courseId;
    BigDecimal durationYears;
    Timestamp createdAt;
    Timestamp updatedAt;
}
