package com.manager.class_activity.qnu.mapper;

import com.manager.class_activity.qnu.dto.request.LecturerRequest;
import com.manager.class_activity.qnu.dto.response.LecturerResponse;
import com.manager.class_activity.qnu.entity.Lecturer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface LecturerMapper {
    @Mappings({
            @Mapping(source = "department.name", target = "departmentName"),
            @Mapping(source = "department.id", target = "departmentId")

    })
    LecturerResponse toLecturerResponse(Lecturer lecturer);

    Lecturer toLecturer(LecturerRequest request);

    void updateLecturerFromRequest(LecturerRequest request, @MappingTarget Lecturer lecturer);
}
