package com.manager.class_activity.qnu.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AcademicAdvisorRequest {
    int lecturerId;
    int classId;
    String academicYear;
}
