package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.dto.request.Filter;
import com.manager.class_activity.qnu.dto.request.LecturerRequest;
import com.manager.class_activity.qnu.dto.response.JsonResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.dto.response.LecturerResponse;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.service.LecturerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/lecturers")
public class LecturerController {
    LecturerService lecturerService;

    @PostMapping("/get-lecturers")
    public JsonResponse<PagedResponse<LecturerResponse>> searchLecturers(@RequestBody CustomPageRequest<Filter> request) {
        PagedResponse<LecturerResponse> response = lecturerService.getLecturers(request);
        return JsonResponse.success(response);
    }

    @PostMapping("/upload")
    public JsonResponse<String> uploadCSV(@RequestParam("file") MultipartFile file) {
        lecturerService.saveLecturers(file);
        return JsonResponse.success("File uploaded and data saved successfully.");
    }

    @GetMapping("/{lecturerId}")
    public JsonResponse<LecturerResponse> getLecturerById(@PathVariable("lecturerId") int lecturerId) {
        return JsonResponse.success(lecturerService.getLecturerResponseById(lecturerId));
    }

    @PutMapping("/{lecturerId}")
    public JsonResponse<String> updateLecturer(@PathVariable("lecturerId") int lecturerId,
                                               @RequestBody LecturerRequest request) {
        lecturerService.updateLecturer(lecturerId, request);
        return JsonResponse.success("Lecturer updated successfully.");
    }

    @PostMapping()
    public JsonResponse<String> createLecturer(@RequestBody LecturerRequest request) {
        lecturerService.saveLecturer(request);
        return JsonResponse.success("Lecturer saved successfully.");
    }

    @DeleteMapping("/{lecturerId}")
    public JsonResponse<String> deleteLecturer(@PathVariable("lecturerId") int lecturerId) {
        lecturerService.deleteLecturer(lecturerId);
        return JsonResponse.success("Lecturer deleted successfully.");
    }
}