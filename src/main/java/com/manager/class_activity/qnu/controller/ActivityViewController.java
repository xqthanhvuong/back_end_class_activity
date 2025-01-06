package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.dto.request.FilterClassActivity;
import com.manager.class_activity.qnu.dto.response.ActivityViewResponse;
import com.manager.class_activity.qnu.dto.response.JsonResponse;
import com.manager.class_activity.qnu.service.ActivityViewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity-view")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActivityViewController {
    ActivityViewService activityViewService;

    @PostMapping("/view/{id}")
    public JsonResponse<String> view(@PathVariable int id) {
        activityViewService.watched(id);
        return JsonResponse.success("Watched");
    }

    @PostMapping("/get-activity-view")
    public JsonResponse<List<ActivityViewResponse>> getActivityView(@RequestBody FilterClassActivity request) {
        return JsonResponse.success(activityViewService.getActivityView(request));
    }


}
