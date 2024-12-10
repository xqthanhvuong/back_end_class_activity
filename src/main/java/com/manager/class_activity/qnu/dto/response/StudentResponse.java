package com.manager.class_activity.qnu.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
    int id;
    String studentCode;
    String name;
    String gender;
    String className;
    int classId;
    String departmentName;
    String courseName;
    String email;
    Date birthDate;
    String studentPosition;
    Timestamp createdAt;
    Timestamp updatedAt;
}
