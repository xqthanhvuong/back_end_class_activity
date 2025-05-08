package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.constant.PermissionConstant;
import com.manager.class_activity.qnu.dto.request.ActivityTimeRequest;
import com.manager.class_activity.qnu.dto.request.FilterClassActivity;
import com.manager.class_activity.qnu.dto.response.ClassActivityResponse;
import com.manager.class_activity.qnu.dto.response.JsonResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.entity.Class;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.service.*;
import com.manager.class_activity.qnu.until.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;


@Log4j2
@RestController
@RequestMapping("/class-activity")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClassActivityController {
    ClassActivityService classActivityService;
    AccountService accountService;
    AttendanceSessionService attendanceSessionService;
    ClassService classService;

    @PostMapping("/get-activities")
    public JsonResponse<PagedResponse<ClassActivityResponse>> searchClasses(@RequestBody CustomPageRequest<FilterClassActivity> request) {
        PagedResponse<ClassActivityResponse> response ;
        log.info("Get activities");
        if("SUPERADMIN".equals((SecurityUtils.getCurrentUserType()))) {
            response = classActivityService.getClassActivities(request);
        }else if("DEPARTMENT".equals((SecurityUtils.getCurrentUserType()))) {
            Integer departmentId = accountService.getDepartmentOfAccount().getId();
            request.getFilter().setDepartmentId(departmentId);
            response = classActivityService.getClassActivities(request);
        }else {
            Integer departmentId = accountService.getDepartmentOfAccount().getId();
            request.getFilter().setDepartmentId(departmentId);
            Integer classId = ObjectUtils.isNotEmpty(accountService.getClassOfAccount()) ?
                    accountService.getClassOfAccount().getId() : null;
            request.getFilter().setClassId(classId);
            response = classActivityService.getClassActivities(request);
        }
        return JsonResponse.success(response);
    }

    @PostMapping("/my-class")
    public JsonResponse<PagedResponse<ClassActivityResponse>> searchMyClasses(@RequestBody CustomPageRequest<FilterClassActivity> request) {
        Class clazz = classService.getClassById(request.getClassId());
        if(attendanceSessionService.isInClass(clazz)){
            return JsonResponse.success(classActivityService.getClassActivities(request));
        }
        throw new BadException(ErrorCode.ACCESS_DENIED);
    }

    @PreAuthorize(PermissionConstant.SET_TIME)
    @PatchMapping("/{id}/activity-time")
    public JsonResponse<String> updateActivityTime(
            @PathVariable int id,
            @RequestBody ActivityTimeRequest request) {
        Timestamp timestamp = Timestamp.from(Instant.parse(request.getActivityTime()));
        classActivityService.updateActivityTime(id, timestamp);

        return JsonResponse.success("Activity time updated successfully.");
    }

    @GetMapping("/{id}")
    public JsonResponse<ClassActivityResponse> getActivity(@PathVariable int id) {
        return JsonResponse.success(classActivityService.getClassActivityResponse(id));
    }
}
