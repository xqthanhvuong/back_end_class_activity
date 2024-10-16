package com.manager.class_activity.qnu.mapper;

import com.manager.class_activity.qnu.dto.request.CourseRequest;
import com.manager.class_activity.qnu.dto.response.CourseResponse;
import com.manager.class_activity.qnu.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseResponse toCourseResponse(Course course);
    void updateCourse(@MappingTarget Course course, CourseRequest courseRequest);
    Course toCourse(CourseRequest courseRequest);
}
