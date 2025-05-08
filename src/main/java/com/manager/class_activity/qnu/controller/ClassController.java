package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.constant.PermissionConstant;
import com.manager.class_activity.qnu.dto.request.ClassRequest;
import com.manager.class_activity.qnu.dto.request.Filter;
import com.manager.class_activity.qnu.dto.request.FilterClass;
import com.manager.class_activity.qnu.dto.response.*;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.service.AccountService;
import com.manager.class_activity.qnu.service.ClassService;
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

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/classes")
public class ClassController {
    ClassService classService;
    AccountService accountService;

    @PreAuthorize(PermissionConstant.CREATE_CLASS)
    @PostMapping("/upload")
    public JsonResponse<String> uploadCSV(@RequestParam("file") MultipartFile file) {
        classService.saveClasses(file);
        return JsonResponse.success("File uploaded and data saved successfully.");
    }

    @PreAuthorize(PermissionConstant.VIEW_CLASS)
    @PostMapping("/get-classes")
    public JsonResponse<PagedResponse<ClassResponse>> searchClasses(@RequestBody CustomPageRequest<FilterClass> request) {
        if(SecurityUtils.isRoleDepartment()){
            Integer departmentId = accountService.getDepartmentOfAccount().getId();
            request.getFilter().setDepartmentId(departmentId);
        }
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

    @PreAuthorize(PermissionConstant.CREATE_CLASS)
    @PostMapping()
    public JsonResponse<String> createClass(@RequestBody ClassRequest request) {
        classService.saveClass(request);
        return JsonResponse.success("Class saved successfully.");
    }

    @PreAuthorize(PermissionConstant.UPDATE_CLASS)
    @PutMapping("/{classId}")
    public JsonResponse<String> updateClass(@PathVariable("classId") int classId,
                                            @RequestBody ClassRequest request) {
        classService.updateClass(classId, request);
        return JsonResponse.success("Class updated successfully.");
    }

    @PreAuthorize(PermissionConstant.DELETE_CLASS)
    @DeleteMapping("/{classId}")
    public JsonResponse<String> deleteClass(@PathVariable("classId") int classId) {
        classService.deleteClass(classId);
        return JsonResponse.success("Class deleted successfully.");
    }

    @GetMapping()
    public  JsonResponse<List<SummaryClassResponse>> getAll(){
        return JsonResponse.success(classService.getSummaryclass());
    }

    @GetMapping("/download-template")
    public ResponseEntity<Resource> downloadTemplate(){
        Resource resource = new ClassPathResource("sample csv/classes_sample.xlsx");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=classes_sample.xlsx");

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
