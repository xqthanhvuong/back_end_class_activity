package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.constant.PermissionConstant;
import com.manager.class_activity.qnu.dto.request.AcademicAdvisorRequest;
import com.manager.class_activity.qnu.dto.request.FilterAdvisor;
import com.manager.class_activity.qnu.dto.response.AcademicAdvisorResponse;
import com.manager.class_activity.qnu.dto.response.JsonResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.entity.Class;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.service.AcademicAdvisorService;
import com.manager.class_activity.qnu.service.AccountService;
import com.manager.class_activity.qnu.until.SecurityUtils;
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

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/academic-advisors")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AcademicAdvisorController {
    AcademicAdvisorService academicAdvisorService;
    AccountService accountService;

    @PreAuthorize(PermissionConstant.VIEW_ADVISOR)
    @GetMapping
    public JsonResponse<List<AcademicAdvisorResponse>> getAllAcademicAdvisors() {
        List<AcademicAdvisorResponse> response = academicAdvisorService.getAll();
        return JsonResponse.success(response);
    }

    @PreAuthorize(PermissionConstant.CREATE_ADVISOR)
    @PostMapping("/upload")
    public JsonResponse<String> uploadCSV(@RequestParam("file") MultipartFile file) {
        academicAdvisorService.saveAdvisors(file);
        return JsonResponse.success("File uploaded and data saved successfully.");
    }

    @PreAuthorize(PermissionConstant.VIEW_ADVISOR)
    @PostMapping("/get-advisors")
    public JsonResponse<PagedResponse<AcademicAdvisorResponse>> searchAdvisors(@RequestBody CustomPageRequest<FilterAdvisor> request) {
        if(SecurityUtils.isRoleDepartment()){
            request.getFilter().setDepartmentId(accountService.getDepartmentOfAccount().getId());
        }else if (SecurityUtils.isRoleStudent()){
            Class clazz = accountService.getClassOfAccount();
            request.getFilter().setClassId(clazz.getId());
        }
        PagedResponse<AcademicAdvisorResponse> response = academicAdvisorService.getAdvisor(request);
        return JsonResponse.success(response);
    }

    @GetMapping("/download-template")
    public ResponseEntity<Resource> downloadTemplate() throws IOException {
        Resource resource = new ClassPathResource("sample csv/academic_advisors_sample.csv");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=advisor_template.csv");

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @PreAuthorize(PermissionConstant.VIEW_ADVISOR)
    @GetMapping("/{id}")
    public JsonResponse<AcademicAdvisorResponse> getAcademicAdvisorById(@PathVariable int id) {
        AcademicAdvisorResponse response = academicAdvisorService.getById(id);
        return JsonResponse.success(response);
    }

    @PreAuthorize(PermissionConstant.CREATE_ADVISOR)
    @PostMapping
    public JsonResponse<String> createAcademicAdvisor(@RequestBody AcademicAdvisorRequest request) {
        academicAdvisorService.save(request);
        return JsonResponse.success("Academic Advisor created successfully.");
    }

    @PreAuthorize(PermissionConstant.UPDATE_ADVISOR)
    @PutMapping("/{id}")
    public JsonResponse<String> updateAcademicAdvisor(@PathVariable int id, @RequestBody AcademicAdvisorRequest request) {
        academicAdvisorService.update(id, request);
        return JsonResponse.success("Academic Advisor updated successfully.");
    }

    @PreAuthorize(PermissionConstant.DELETE_ADVISOR)
    @DeleteMapping("/{id}")
    public JsonResponse<String> deleteAcademicAdvisor(@PathVariable int id) {
        academicAdvisorService.delete(id);
        return JsonResponse.success("Academic Advisor deleted successfully.");
    }

    @GetMapping("/academic-years")
    public JsonResponse<List<String>> academicYears(){
        return JsonResponse.success(academicAdvisorService.getAcademicYears());
    }
}
