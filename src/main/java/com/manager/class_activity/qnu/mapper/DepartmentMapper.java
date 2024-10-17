package com.manager.class_activity.qnu.mapper;

import com.manager.class_activity.qnu.dto.request.DepartmentRequest;
import com.manager.class_activity.qnu.dto.response.DepartmentResponse;
import com.manager.class_activity.qnu.dto.response.SummaryDepartmentResponse;
import com.manager.class_activity.qnu.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    DepartmentResponse toDepartmentResponse(Department department);

    void updateDepartment(@MappingTarget Department department, DepartmentRequest departmentUpdateRequest);

    Department toDepartment(DepartmentRequest departmentUpdateRequest);

    SummaryDepartmentResponse toSummaryDepartmentResponse(Department department);
}
