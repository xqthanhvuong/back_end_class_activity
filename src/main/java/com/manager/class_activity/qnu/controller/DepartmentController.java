package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.dto.request.DepartmentRequest;
import com.manager.class_activity.qnu.dto.request.Filter;
import com.manager.class_activity.qnu.dto.response.DepartmentResponse;
import com.manager.class_activity.qnu.dto.response.JsonResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.dto.response.SummaryDepartmentResponse;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.service.DepartmentService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/departments")
public class DepartmentController {
    private static final Logger log = LoggerFactory.getLogger(DepartmentController.class);
    DepartmentService departmentService;

    @PostMapping("/upload")
    public JsonResponse<String> uploadCSV(@RequestParam("file") MultipartFile file) {
        departmentService.saveDepartments(file);
        return JsonResponse.success("File uploaded and data saved successfully.");
    }

    @GetMapping("/download-template")
    public ResponseEntity<Resource> downloadTemplate() throws IOException {
        Resource resource = new ClassPathResource("sample csv/departments.csv");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=departments_template.csv");

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @PostMapping("/get-departments")
    public JsonResponse<PagedResponse<DepartmentResponse>> searchDepartments(@RequestBody CustomPageRequest<Filter> request) {
        PagedResponse<DepartmentResponse> response = departmentService.getDepartments(request);
        log.info(request.getKeyWord());
        return JsonResponse.success(response);
    }

    @GetMapping("/{departmentId}")
    public JsonResponse<DepartmentResponse> getDepartmentById(@PathVariable("departmentId") int departmentId) {
        return JsonResponse.success(departmentService.getDepartmentResponseById(departmentId));
    }

    @PutMapping("/{departmentId}")
    public JsonResponse<String> updateDepartment(@PathVariable("departmentId") int departmentId,
                                                 @RequestBody DepartmentRequest request){
        departmentService.updateDepartment(departmentId, request);
        return JsonResponse.success("Department updated successfully.");
    }

    @PostMapping()
    public JsonResponse<String> createDepartment(@RequestBody DepartmentRequest request) {
        departmentService.saveDepartment(request);
        return JsonResponse.success("Department saved successfully.");
    }

    @DeleteMapping("/{departmentId}")
    public JsonResponse<String> deleteDepartment(@PathVariable("departmentId") int departmentId) {
        departmentService.deleteDepartment(departmentId);
        return JsonResponse.success("Department deleted successfully.");
    }

    @GetMapping()
    public JsonResponse<List<SummaryDepartmentResponse>> getDepartmentSummary() {
        return JsonResponse.success(departmentService.getSummaryDepartments());
    }
}

