package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.dto.request.MinutesActivityRequest;
import com.manager.class_activity.qnu.dto.response.JsonResponse;
import com.manager.class_activity.qnu.dto.response.MinutesActivityResponse;
import com.manager.class_activity.qnu.service.MinutesActivitiesService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/minute-activity")
public class MinutesActivitiesController {
    MinutesActivitiesService minutesActivitiesService;

    @PostMapping()
    public JsonResponse<String> createMinutesActivity(@RequestBody MinutesActivityRequest request){
        minutesActivitiesService.save(request);
        return JsonResponse.success("create success");
    }

    @GetMapping("/{id}")
    public JsonResponse<MinutesActivityResponse> getMinutesActivity(@PathVariable Integer id){
        return JsonResponse.success(minutesActivitiesService.getMinutesActivity(id));
    }
}
