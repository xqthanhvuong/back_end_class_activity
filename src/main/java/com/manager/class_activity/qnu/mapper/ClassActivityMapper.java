package com.manager.class_activity.qnu.mapper;

import com.manager.class_activity.qnu.dto.response.ClassActivityResponse;
import com.manager.class_activity.qnu.entity.ClassActivity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ClassActivityMapper {
    @Mappings({
            @Mapping(source = "clazz.name", target = "className"),
            @Mapping(source = "leader.name", target = "leader"),
            @Mapping(source = "activity.id",target = "activityId"),
            @Mapping(source = "clazz.id",target = "classId")
    })
    ClassActivityResponse toClassActivityResponse(ClassActivity classActivity);
}
