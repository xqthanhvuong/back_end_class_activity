package com.manager.class_activity.qnu.mapper;

import com.manager.class_activity.qnu.dto.request.StudentRequest;
import com.manager.class_activity.qnu.dto.response.StudentOfClass;
import com.manager.class_activity.qnu.dto.response.StudentResponse;
import com.manager.class_activity.qnu.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    @Mappings({
            @Mapping(source = "clazz.department.name", target = "departmentName"),
            @Mapping(source = "clazz.course.name", target = "courseName"),
            @Mapping(source = "clazz.name", target = "className"),
            @Mapping(source = "clazz.id", target = "classId")
    })
    StudentResponse toStudentResponse(Student student);
    Student toStudent(StudentRequest request);
    void updateStudent(@MappingTarget Student student,StudentRequest request);
    StudentOfClass toStudentOfClass(Student student);
}
