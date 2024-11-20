package com.manager.class_activity.qnu.controller;

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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/courses")
public class CourseController {
    CourseService courseService;

    @PostMapping("/upload")
    public JsonResponse<String> uploadCSV(@RequestParam("file") MultipartFile file) {
        courseService.saveCourses(file);
        return JsonResponse.success("File uploaded and data saved successfully.");
    }

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
    public ResponseEntity<Resource> downloadTemplate() throws IOException {
        Resource resource = new ClassPathResource("sample csv/departments.csv");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=departments_template.csv");

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @PutMapping("/{courseId}")
    public JsonResponse<String> updateCourse(@PathVariable("courseId") int courseId,
                                             @RequestBody CourseRequest request) {
        courseService.updateCourse(courseId, request);
        return JsonResponse.success("Course updated successfully.");
    }

    @PostMapping()
    public JsonResponse<String> createCourse(@RequestBody CourseRequest request) {
        courseService.saveCourse(request);
        return JsonResponse.success("Course saved successfully.");
    }

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
