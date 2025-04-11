package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.constant.PermissionConstant;
import com.manager.class_activity.qnu.dto.request.CourseRequest;
import com.manager.class_activity.qnu.dto.request.Filter;
import com.manager.class_activity.qnu.dto.response.*;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.service.CourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/courses")
public class CourseController {
    CourseService courseService;

    @PreAuthorize(PermissionConstant.CREATE_COURSE)
    @PostMapping("/upload")
    public JsonResponse<String> uploadCSV(@RequestParam("file") MultipartFile file) {
        courseService.saveCourses(file);
        return JsonResponse.success("File uploaded and data saved successfully.");
    }

    @PreAuthorize(PermissionConstant.VIEW_COURSE)
    @PostMapping("/get-courses")
    public JsonResponse<PagedResponse<CourseResponse>> searchCourses(@RequestBody CustomPageRequest<Filter> request) {
        PagedResponse<CourseResponse> response = courseService.getCourses(request);
        return JsonResponse.success(response);
    }

    @GetMapping("/{courseId}")
    public JsonResponse<CourseResponse> getCourseById(@PathVariable("courseId") int courseId) {
        return JsonResponse.success(courseService.getCourseResponseById(courseId));
    }

    @GetMapping("/download-template")
    public ResponseEntity<Resource> downloadTemplate() {
        Resource resource = new ClassPathResource("sample csv/batch_sample.xlsx");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=batch_sample.xlsx");

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @PreAuthorize(PermissionConstant.UPDATE_COURSE)
    @PutMapping("/{courseId}")
    public JsonResponse<String> updateCourse(@PathVariable("courseId") int courseId,
                                             @RequestBody CourseRequest request) {
        courseService.updateCourse(courseId, request);
        return JsonResponse.success("Course updated successfully.");
    }

    @PreAuthorize(PermissionConstant.CREATE_COURSE)
    @PostMapping()
    public JsonResponse<String> createCourse(@RequestBody CourseRequest request) {
        courseService.saveCourse(request);
        return JsonResponse.success("Course saved successfully.");
    }

    @PreAuthorize(PermissionConstant.DELETE_COURSE)
    @DeleteMapping("/{courseId}")
    public JsonResponse<String> deleteCourse(@PathVariable("courseId") int courseId) {
        courseService.deleteCourse(courseId);
        return JsonResponse.success("Course deleted successfully.");
    }

    @GetMapping()
    public JsonResponse<List<SummaryCourseResponse>> getDepartmentSummary() {
        return JsonResponse.success(courseService.getSummaryCourses());
    }
}
