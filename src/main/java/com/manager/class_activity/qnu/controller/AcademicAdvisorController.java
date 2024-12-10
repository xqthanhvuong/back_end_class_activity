package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.dto.request.AcademicAdvisorRequest;
import com.manager.class_activity.qnu.dto.request.FilterAdvisor;
import com.manager.class_activity.qnu.dto.response.AcademicAdvisorResponse;
import com.manager.class_activity.qnu.dto.response.JsonResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.service.AcademicAdvisorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/academic-advisors")
public class AcademicAdvisorController {

    private final AcademicAdvisorService academicAdvisorService;

    @PreAuthorize("hasRole('VIEW_ADVISOR')")
    @GetMapping
    public JsonResponse<List<AcademicAdvisorResponse>> getAllAcademicAdvisors() {
        List<AcademicAdvisorResponse> response = academicAdvisorService.getAll();
        return JsonResponse.success(response);
    }

    @PreAuthorize("hasRole('CREATE_ADVISOR')")
    @PostMapping("/upload")
    public JsonResponse<String> uploadCSV(@RequestParam("file") MultipartFile file) {
        academicAdvisorService.saveAdvisors(file);
        return JsonResponse.success("File uploaded and data saved successfully.");
    }

    @PreAuthorize("hasRole('VIEW_ADVISOR')")
    @PostMapping("/get-advisors")
    public JsonResponse<PagedResponse<AcademicAdvisorResponse>> searchAdvisors(@RequestBody CustomPageRequest<FilterAdvisor> request) {
        PagedResponse<AcademicAdvisorResponse> response = academicAdvisorService.getAdvisor(request);
        return JsonResponse.success(response);
    }

    @PreAuthorize("hasRole('VIEW_ADVISOR')")
    @GetMapping("/{id}")
    public JsonResponse<AcademicAdvisorResponse> getAcademicAdvisorById(@PathVariable int id) {
        AcademicAdvisorResponse response = academicAdvisorService.getById(id);
        return JsonResponse.success(response);
    }

    @PreAuthorize("hasRole('CREATE_ADVISOR')")
    @PostMapping
    public JsonResponse<String> createAcademicAdvisor(@RequestBody AcademicAdvisorRequest request) {
        academicAdvisorService.save(request);
        return JsonResponse.success("Academic Advisor created successfully.");
    }

    @PreAuthorize("hasRole('UPDATE_ADVISOR')")
    @PutMapping("/{id}")
    public JsonResponse<String> updateAcademicAdvisor(@PathVariable int id, @RequestBody AcademicAdvisorRequest request) {
        academicAdvisorService.update(id, request);
        return JsonResponse.success("Academic Advisor updated successfully.");
    }

    @PreAuthorize("hasRole('DELETE_ADVISOR')")
    @DeleteMapping("/{id}")
    public JsonResponse<String> deleteAcademicAdvisor(@PathVariable int id) {
        academicAdvisorService.delete(id);
        return JsonResponse.success("Academic Advisor deleted successfully.");
    }
}
