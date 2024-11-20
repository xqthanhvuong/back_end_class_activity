package com.manager.class_activity.qnu.mapper;

import com.manager.class_activity.qnu.dto.request.ClassRequest;
import com.manager.class_activity.qnu.dto.response.ClassResponse;
import com.manager.class_activity.qnu.dto.response.SummaryClassResponse;
import com.manager.class_activity.qnu.entity.Class;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ClassMapper {
    @Mappings({
            @Mapping(source = "department.name", target = "departmentName"),
            @Mapping(source = "department.id", target = "departmentId"),
            @Mapping(source = "course.name", target = "courseName"),
            @Mapping(source = "course.id", target = "courseId")
    })
    ClassResponse toClassResponse(Class clazz);
    @Mapping(target = "durationYears", ignore = true)
    void updateClass(@MappingTarget Class clazz, ClassRequest classRequest);
    @Mapping(target = "durationYears", ignore = true)
    Class toClass(ClassRequest classRequest);
    SummaryClassResponse toSummaryClassResponse(Class clazz);
}
