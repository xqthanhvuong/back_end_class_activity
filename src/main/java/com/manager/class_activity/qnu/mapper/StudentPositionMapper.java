package com.manager.class_activity.qnu.mapper;

import com.manager.class_activity.qnu.dto.request.StudentPositionRequest;
import com.manager.class_activity.qnu.entity.StudentPosition;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudentPositionMapper {
    StudentPosition toStudentPosition(StudentPositionRequest request);
}
