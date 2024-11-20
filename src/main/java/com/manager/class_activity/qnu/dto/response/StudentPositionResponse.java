package com.manager.class_activity.qnu.dto.response;

import com.manager.class_activity.qnu.entity.StudentPositionEnum;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data
@Builder
public class StudentPositionResponse {
    int id;
    int studentId;
    int classId;
    StudentPositionEnum position;
    Date startDate;
    Date endDate;
}
