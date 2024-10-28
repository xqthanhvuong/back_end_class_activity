package com.manager.class_activity.qnu.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class AcademicAdvisorResponse {
    int id;
    String lecturerName;
    List<String> className;
    String academicYear;
    String createdAt;
    String updatedAt;
}
