package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.dto.request.StudentPositionRequest;
import com.manager.class_activity.qnu.dto.response.JsonResponse;
import com.manager.class_activity.qnu.dto.response.StudentPositionResponse;
import com.manager.class_activity.qnu.service.StudentPositionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/positions")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentPositionController {

    StudentPositionService studentPositionService;

    @PostMapping
    public JsonResponse<String> createPosition(@RequestBody StudentPositionRequest request) {
        studentPositionService.saveStudentPosition(request);
        return JsonResponse.success("Position saved successfully.");
    }

//    @DeleteMapping("/{id}")
//    public JsonResponse<String> deletePosition(@PathVariable int id) {
//        studentPositionService.deleteStudentPosition(id);
//        return JsonResponse.success("Position deleted successfully.");
//    }
}
