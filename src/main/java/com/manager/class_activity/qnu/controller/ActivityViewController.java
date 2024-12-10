package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.dto.response.JsonResponse;
import com.manager.class_activity.qnu.entity.ActivityView;
import com.manager.class_activity.qnu.service.ActivityViewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
