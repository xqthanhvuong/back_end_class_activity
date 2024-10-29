package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.dto.request.AcademicAdvisorRequest;
import com.manager.class_activity.qnu.dto.request.FilterAdvisor;
import com.manager.class_activity.qnu.dto.request.FilterClass;
import com.manager.class_activity.qnu.dto.response.AcademicAdvisorResponse;
import com.manager.class_activity.qnu.dto.response.ClassResponse;
import com.manager.class_activity.qnu.dto.response.JsonResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.service.AcademicAdvisorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/academic-advisors")
public class AcademicAdvisorController {

    private final AcademicAdvisorService academicAdvisorService;

    @GetMapping
    public JsonResponse<List<AcademicAdvisorResponse>> getAllAcademicAdvisors() {
        List<AcademicAdvisorResponse> response = academicAdvisorService.getAll();
        return JsonResponse.success(response);
    }

//    @PostMapping("/get-advisors")
//    public JsonResponse<PagedResponse<AcademicAdvisorResponse>> searchAdvisors(@RequestBody CustomPageRequest<FilterAdvisor> request) {
//        PagedResponse<AcademicAdvisorResponse> response = academicAdvisorService.getAdvisor(request);
//        return JsonResponse.success(response);
//    }

    @GetMapping("/{id}")
    public JsonResponse<AcademicAdvisorResponse> getAcademicAdvisorById(@PathVariable int id) {
        AcademicAdvisorResponse response = academicAdvisorService.getById(id);
        return JsonResponse.success(response);
    }

    @PostMapping
    public JsonResponse<String> createAcademicAdvisor(@RequestBody AcademicAdvisorRequest request) {
        academicAdvisorService.save(request);
        return JsonResponse.success("Academic Advisor created successfully.");
    }

    @PutMapping("/{id}")
    public JsonResponse<String> updateAcademicAdvisor(@PathVariable int id, @RequestBody AcademicAdvisorRequest request) {
        academicAdvisorService.update(id, request);
        return JsonResponse.success("Academic Advisor updated successfully.");
    }

    @DeleteMapping("/{id}")
    public JsonResponse<String> deleteAcademicAdvisor(@PathVariable int id) {
        academicAdvisorService.delete(id);
        return JsonResponse.success("Academic Advisor deleted successfully.");
    }
}
