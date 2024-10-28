package com.manager.class_activity.qnu.dto.request;

import lombok.Data;

@Data
public class AcademicAdvisorRequest {
    int lecturerId;
    int classId;
    String academicYear;
}
