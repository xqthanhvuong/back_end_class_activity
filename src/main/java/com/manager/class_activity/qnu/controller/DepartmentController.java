package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.dto.request.DepartmentRequest;
import com.manager.class_activity.qnu.dto.request.Filter;
import com.manager.class_activity.qnu.dto.response.DepartmentResponse;
import com.manager.class_activity.qnu.dto.response.JsonResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.dto.response.SummaryDepartmentResponse;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.service.DepartmentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/departments")
public class DepartmentController {
    DepartmentService departmentService;

    @PostMapping("/upload")
    public JsonResponse<String> uploadCSV(@RequestParam("file") MultipartFile file) {
        departmentService.saveDepartments(file);
        return JsonResponse.success("File uploaded and data saved successfully.");
    }

    @PostMapping("/get-departments")
    public JsonResponse<PagedResponse<DepartmentResponse>> searchDepartments(@RequestBody CustomPageRequest<Filter> request) {
        PagedResponse<DepartmentResponse> response = departmentService.getDepartments(request);
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

