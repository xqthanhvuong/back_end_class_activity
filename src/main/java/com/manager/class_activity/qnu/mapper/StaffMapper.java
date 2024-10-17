package com.manager.class_activity.qnu.mapper;

import com.manager.class_activity.qnu.dto.request.StaffRequest;
import com.manager.class_activity.qnu.dto.response.StaffResponse;
import com.manager.class_activity.qnu.entity.Staff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface StaffMapper {
    @Mappings({
            @Mapping(source = "department.name", target = "departmentName")
    })
    StaffResponse toStaffResponse(Staff staff);
    Staff toStaff(StaffRequest request);
    void updateStaffFromRequest(StaffRequest request, @MappingTarget Staff staff);
}
