package com.manager.class_activity.qnu.mapper;

import com.manager.class_activity.qnu.dto.response.AcademicAdvisorResponse;
import com.manager.class_activity.qnu.entity.AcademicAdvisor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AcademicAdvisorMapper {
    @Mapping(source = "lecturer.name", target = "lecturerName")
    @Mapping(source = "clazz.name", target = "className")
    AcademicAdvisorResponse toResponse(AcademicAdvisor advisor);
}
