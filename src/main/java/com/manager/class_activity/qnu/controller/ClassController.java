package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.dto.request.ClassRequest;
import com.manager.class_activity.qnu.dto.request.Filter;
import com.manager.class_activity.qnu.dto.request.FilterClass;
import com.manager.class_activity.qnu.dto.response.*;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.service.ClassService;
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
@RequestMapping("/classes")
public class ClassController {
    ClassService classService;

    @PostMapping("/upload")
    public JsonResponse<String> uploadCSV(@RequestParam("file") MultipartFile file) {
        classService.saveClasses(file);
        return JsonResponse.success("File uploaded and data saved successfully.");
    }

    @PreAuthorize("hasRole('VIEW_CLASS')")
    @PostMapping("/get-classes")
    public JsonResponse<PagedResponse<ClassResponse>> searchClasses(@RequestBody CustomPageRequest<FilterClass> request) {
        PagedResponse<ClassResponse> response = classService.getClasses(request);
        return JsonResponse.success(response);
    }

    @PostMapping("/my-class/for-student")
    public JsonResponse<ClassDetailResponse> getMyClass(@RequestBody CustomPageRequest<Filter> request) {
        return JsonResponse.success(classService.getMyClass(request));
    }

    @GetMapping("/my-class/for-lecturer")
    public JsonResponse<List<ClassResponse>> getMyClassLecturer(){
        return JsonResponse.success(classService.getMyClassLecturer());
    }

    @PostMapping("/{classId}")
    public JsonResponse<ClassDetailResponse> getClassById(@PathVariable("classId") int classId, @RequestBody CustomPageRequest<Filter> request) {
        return JsonResponse.success(classService.getClassDetailById(classId, request));
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

    @GetMapping()
    public  JsonResponse<List<SummaryClassResponse>> getAll(){
        return JsonResponse.success(classService.getSummaryclass());
    }
}
