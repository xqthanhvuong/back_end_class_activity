package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.dto.request.FilterClass;
import com.manager.class_activity.qnu.dto.request.StaffRequest;
import com.manager.class_activity.qnu.dto.response.JsonResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.dto.response.StaffResponse;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.service.AccountService;
import com.manager.class_activity.qnu.service.StaffService;
import com.manager.class_activity.qnu.until.SecurityUtils;
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

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/staffs")
public class StaffController {
    StaffService staffService;
    AccountService accountService;
    @PostMapping("/get-staffs")
    public JsonResponse<PagedResponse<StaffResponse>> searchStaffs(@RequestBody CustomPageRequest<FilterClass> request) {
        if(SecurityUtils.isRoleDepartment()){
            Integer departmentId = accountService.getDepartmentOfAccount().getId();
            request.getFilter().setDepartmentId(departmentId);
        }
        PagedResponse<StaffResponse> response = staffService.getStaffs(request);
        return JsonResponse.success(response);
    }

    @PostMapping("/upload")
    public JsonResponse<String> uploadCSV(@RequestParam("file") MultipartFile file) {
        staffService.saveStaffs(file);
        return JsonResponse.success("File uploaded and data saved successfully.");
    }

    @GetMapping("/{staffId}")
    public JsonResponse<StaffResponse> getStaffById(@PathVariable("staffId") int staffId) {
        return JsonResponse.success(staffService.getStaffResponseById(staffId));
    }

    @PutMapping("/{staffId}")
    public JsonResponse<String> updateStaff(@PathVariable("staffId") int staffId,
                                            @RequestBody StaffRequest request){
        staffService.updateStaff(staffId, request);
        return JsonResponse.success("Staff updated successfully.");
    }

    @PostMapping()
    public JsonResponse<String> createStaff(@RequestBody StaffRequest request) {
        staffService.saveStaff(request);
        return JsonResponse.success("Staff saved successfully.");
    }

    @DeleteMapping("/{staffId}")
    public JsonResponse<String> deleteStaff(@PathVariable("staffId") int staffId) {
        staffService.deleteStaff(staffId);
        return JsonResponse.success("Staff deleted successfully.");
    }

    @GetMapping("/download-template")
    public ResponseEntity<Resource> downloadTemplate() throws IOException {
        Resource resource = new ClassPathResource("sample csv/staff_sample.xlsx");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=staff_sample.xlsx");

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }


}
