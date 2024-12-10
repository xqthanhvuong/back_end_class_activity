package com.manager.class_activity.qnu.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manager.class_activity.qnu.dto.request.FileRequest;
import com.manager.class_activity.qnu.dto.request.Filter;
import com.manager.class_activity.qnu.dto.response.*;
import com.manager.class_activity.qnu.entity.Activity;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.service.ActivityGuideService;
import com.manager.class_activity.qnu.service.ActivityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/activity-guide")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActivityGuideController {
    ActivityGuideService activityGuideService;
    ActivityService activityService;

    @PostMapping("/get-guides")
    public JsonResponse<PagedResponse<GuideResponse>> searchClasses(@RequestBody CustomPageRequest<Filter> request) {
        PagedResponse<GuideResponse> response = activityGuideService.getActivitiesByPage(request);
        return JsonResponse.success(response);
    }

    @GetMapping()
    public JsonResponse<List<ActivityGuideSummary>> getActivitiesSummary() {
        return JsonResponse.success(activityGuideService.getAllSummary());
    }

    @PostMapping("/{id}")
    public JsonResponse<String> addActivityGuide(@PathVariable int id,
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
            activityGuideService.createActivityGuide(file, activity);
        }
        return JsonResponse.success("Add activity guide successfully.");

    }

    @DeleteMapping("/{id}")
    public JsonResponse<String> deleteActivityGuide(@PathVariable int id) {
        activityGuideService.deleteActivityGuide(id);
        return JsonResponse.success("Delete success");
    }

    @GetMapping("/get-all-guide/{id}")
    public JsonResponse<List<GuideResponse>> getAllGuide(@PathVariable int id) {
        return JsonResponse.success(activityGuideService.getAllActivityGuides(id));
    }




}
