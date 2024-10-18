package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.dto.request.ClassRequest;
import com.manager.class_activity.qnu.dto.request.Filter;
import com.manager.class_activity.qnu.dto.request.FilterClass;
import com.manager.class_activity.qnu.dto.response.ClassResponse;
import com.manager.class_activity.qnu.dto.response.JsonResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.service.ClassService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/classes")
public class ClassController {
    ClassService classService;

    @PostMapping("/upload")
    public JsonResponse<String> uploadCSV(@RequestParam("file") MultipartFile file) {
        classService.saveClasses(file);
        return JsonResponse.success("File uploaded and data saved successfully.");
    }

    @PostMapping("/get-classes")
    public JsonResponse<PagedResponse<ClassResponse>> searchClasses(@RequestBody CustomPageRequest<FilterClass> request) {
        PagedResponse<ClassResponse> response = classService.getClasses(request);
        return JsonResponse.success(response);
    }

    @GetMapping("/{classId}")
    public JsonResponse<ClassResponse> getClassById(@PathVariable("classId") int classId) {
        return JsonResponse.success(classService.getClassResponseById(classId));
    }

    @PostMapping()
    public JsonResponse<String> createClass(@RequestBody ClassRequest request) {
        classService.saveClass(request);
        return JsonResponse.success("Class saved successfully.");
    }

    @PutMapping("/{classId}")
    public JsonResponse<String> updateClass(@PathVariable("classId") int classId,
                                            @RequestBody ClassRequest request) {
        classService.updateClass(classId, request);
        return JsonResponse.success("Class updated successfully.");
    }

    @DeleteMapping("/{classId}")
    public JsonResponse<String> deleteClass(@PathVariable("classId") int classId) {
        classService.deleteClass(classId);
        return JsonResponse.success("Class deleted successfully.");
    }
}
