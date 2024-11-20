package com.manager.class_activity.qnu.dto.request;

import com.manager.class_activity.qnu.entity.StudentPositionEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentPositionRequest {
    @NotNull
    int studentId;

    @NotNull
    int classId;

    @NotNull
    StudentPositionEnum position;
}
