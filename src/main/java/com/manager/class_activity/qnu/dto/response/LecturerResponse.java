package com.manager.class_activity.qnu.dto.response;

import com.manager.class_activity.qnu.entity.GenderEnum;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.util.Date;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LecturerResponse {
    int id;
    String name;
    GenderEnum gender;
    String phoneNumber;
    String email;
    Date birthDate;
    String degree;
    String departmentName;
    Timestamp createdAt;
    Timestamp updatedAt;
}
