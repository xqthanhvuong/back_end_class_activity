package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.dto.request.ActivityTimeRequest;
import com.manager.class_activity.qnu.dto.request.FilterClassActivity;
import com.manager.class_activity.qnu.dto.response.ActivityResponse;
import com.manager.class_activity.qnu.dto.response.ClassActivityResponse;
import com.manager.class_activity.qnu.dto.response.JsonResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.service.AccountService;
import com.manager.class_activity.qnu.service.ClassActivityService;
import com.manager.class_activity.qnu.until.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

@RestController
@RequestMapping("/class-activity")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClassActivityController {
    ClassActivityService classActivityService;
    AccountService accountService;

    @PostMapping("/get-activities")
    public JsonResponse<PagedResponse<ClassActivityResponse>> searchClasses(@RequestBody CustomPageRequest<FilterClassActivity> request) {
        PagedResponse<ClassActivityResponse> response ;
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

    @PreAuthorize("hasRole('SET_TIME')")
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
