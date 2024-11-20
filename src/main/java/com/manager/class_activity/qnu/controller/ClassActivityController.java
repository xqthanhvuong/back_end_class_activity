package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.dto.request.FilterClass;
import com.manager.class_activity.qnu.dto.request.FilterStudent;
import com.manager.class_activity.qnu.dto.response.ClassActivityResponse;
import com.manager.class_activity.qnu.dto.response.ClassResponse;
import com.manager.class_activity.qnu.dto.response.JsonResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.service.ClassActivityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/class-activity")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClassActivityController {
    ClassActivityService classActivityService;
    @PostMapping("/create-activity")
    public JsonResponse<String> createClassActivity(@RequestParam("file") MultipartFile file) {
        classActivityService.createAllClassActivity(file);
        return JsonResponse.success("File uploaded and data saved successfully.");
    }

    @PostMapping("/get-activities")
    public JsonResponse<PagedResponse<ClassActivityResponse>> searchClasses(@RequestBody CustomPageRequest<FilterStudent> request) {
        PagedResponse<ClassActivityResponse> response = classActivityService.getClassActivities(request);
        return JsonResponse.success(response);
    }
}
