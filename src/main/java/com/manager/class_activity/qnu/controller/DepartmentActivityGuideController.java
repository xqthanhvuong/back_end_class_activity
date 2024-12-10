package com.manager.class_activity.qnu.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manager.class_activity.qnu.dto.request.FileRequest;
import com.manager.class_activity.qnu.dto.request.Filter;
import com.manager.class_activity.qnu.dto.response.GuideResponse;
import com.manager.class_activity.qnu.dto.response.JsonResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.entity.Activity;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.service.ActivityService;
import com.manager.class_activity.qnu.service.DepartmentGuideService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/department-guide")
public class DepartmentActivityGuideController {
    DepartmentGuideService departmentGuideService;
    ActivityService activityService;

    @PreAuthorize("hasRole('CREATE_DEPARTMENT_GUIDE')")
    @PostMapping("/add-guide/{id}")
    public JsonResponse<String> uploadCSV(@PathVariable int id,
                                          @RequestPart("files") List<MultipartFile> files,
                                          @RequestPart("metadata") String metadata) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        List<FileRequest> fileRequests = objectMapper.readValue(metadata, new TypeReference<List<FileRequest>>() {});
        Activity activity = activityService.getActivityById(id);
        if (files.size() != fileRequests.size()) {
            throw new IllegalArgumentException("Number of files and metadata entries must match.");
        }

        for (int i = 0; i < files.size(); i++) {
            fileRequests.get(i).setFile(files.get(i));
        }
        for (FileRequest file: fileRequests) {
            departmentGuideService.addDepartmentActivityGuide(file, activity);
        }
        return JsonResponse.success("File uploaded and data saved successfully.");
    }

    @PostMapping("/get-guides")
    public JsonResponse<PagedResponse<GuideResponse>> searchClasses(@RequestBody CustomPageRequest<Filter> request) {
        PagedResponse<GuideResponse> response = departmentGuideService.getActivitiesByPage(request);
        return JsonResponse.success(response);
    }

    @DeleteMapping("/{id}")
    public JsonResponse<String> delete(@PathVariable int id) {
        departmentGuideService.deleteGuide(id);
        return JsonResponse.success("Deleted department activity guide successfully.");
    }
}
