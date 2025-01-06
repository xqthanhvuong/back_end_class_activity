package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.dto.request.StudentRequest;
import com.manager.class_activity.qnu.dto.request.FilterStudent;
import com.manager.class_activity.qnu.dto.response.StudentResponse;
import com.manager.class_activity.qnu.dto.response.JsonResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.service.AccountService;
import com.manager.class_activity.qnu.service.StudentService;
import com.manager.class_activity.qnu.until.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/students")
public class StudentController {
    StudentService studentService;
    AccountService accountService;

    @PostMapping("/get-students")
    public JsonResponse<PagedResponse<StudentResponse>> searchStudents(@RequestBody CustomPageRequest<FilterStudent> request) {
        if(SecurityUtils.isRoleDepartment()){
            Integer departmentId = accountService.getDepartmentOfAccount().getId();
            request.getFilter().setDepartmentId(departmentId);
        }
        PagedResponse<StudentResponse> response = studentService.getStudents(request);
        return JsonResponse.success(response);
    }

    @GetMapping("/{studentId}")
    public JsonResponse<StudentResponse> getStudentById(@PathVariable("studentId") int studentId) {
        return JsonResponse.success(studentService.getStudentResponseById(studentId));
    }

    @PostMapping("/upload")
    public JsonResponse<String> uploadCSV(@RequestParam("file") MultipartFile file) {
        studentService.saveStudents(file);
        return JsonResponse.success("File uploaded and data saved successfully.");
    }

    @PostMapping()
    public JsonResponse<String> createStudent(@RequestBody StudentRequest request) {
        studentService.saveStudent(request);
        return JsonResponse.success("Student saved successfully.");
    }

    @PutMapping("/{studentId}")
    public JsonResponse<String> updateStudent(@PathVariable("studentId") int studentId,
                                              @RequestBody StudentRequest request) {
        studentService.updateStudent(studentId, request);
        return JsonResponse.success("Student updated successfully.");
    }

    @DeleteMapping("/{studentId}")
    public JsonResponse<String> deleteStudent(@PathVariable("studentId") int studentId) {
        studentService.deleteStudent(studentId);
        return JsonResponse.success("Student deleted successfully.");
    }
}
