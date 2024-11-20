package com.manager.class_activity.qnu.dto;

import com.manager.class_activity.qnu.entity.Lecturer;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AcademicAdvisorDTO {
    Lecturer lecturer;
    String academicYear;
}
