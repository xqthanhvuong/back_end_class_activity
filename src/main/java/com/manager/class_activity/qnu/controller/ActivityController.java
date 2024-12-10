package com.manager.class_activity.qnu.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manager.class_activity.qnu.dto.request.FileRequest;
import com.manager.class_activity.qnu.dto.request.Filter;
import com.manager.class_activity.qnu.dto.response.ActivityResponse;
import com.manager.class_activity.qnu.dto.response.ActivitySummary;
import com.manager.class_activity.qnu.dto.response.JsonResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.service.ActivityService;
import io.swagger.v3.core.util.Json;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/activity")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActivityController {
    ActivityService activityService;

    @PostMapping("/create-activity")
    public JsonResponse<String> createClassActivity(@RequestPart("files") List<MultipartFile> files,
                                                    @RequestPart("metadata") String metadata) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<FileRequest> fileRequests = objectMapper.readValue(metadata, new TypeReference<List<FileRequest>>() {});

        if (files.size() != fileRequests.size()) {
            throw new IllegalArgumentException("Number of files and metadata entries must match.");
        }

        for (int i = 0; i < files.size(); i++) {
            fileRequests.get(i).setFile(files.get(i));
        }
        activityService.createClassActivity(fileRequests);

        return JsonResponse.success("File uploaded and data saved successfully.");
    }

    @PostMapping("/get-activities")
    public JsonResponse<PagedResponse<ActivityResponse>> getActivities(@RequestBody CustomPageRequest<Filter> request){
        return JsonResponse.success(activityService.getActivities(request));
    }

    @GetMapping()
    public JsonResponse<List<ActivitySummary>> getAllActivities(){
        return JsonResponse.success(activityService.getAllSummary());
    }






}
