package com.manager.class_activity.qnu.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class AcademicAdvisorResponse {
    int id;
    String lecturerName;
    int lecturerId;
    String className;
    int classId;
    String academicYear;
    String createdAt;
    String updatedAt;
}
