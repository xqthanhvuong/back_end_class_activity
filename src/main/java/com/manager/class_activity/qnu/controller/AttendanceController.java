package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.dto.request.AttendanceRecordRequest;
import com.manager.class_activity.qnu.dto.request.RollCallRequest;
import com.manager.class_activity.qnu.dto.response.AttendanceRecordResponse;
import com.manager.class_activity.qnu.dto.response.JsonResponse;
import com.manager.class_activity.qnu.dto.response.RollCallResponse;
import com.manager.class_activity.qnu.service.AttendanceSessionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttendanceController {
    AttendanceSessionService attendanceSessionService;

    @PostMapping("/{id}")
    public JsonResponse<String> createAttendanceSession(@PathVariable int id) {
        return JsonResponse.success(attendanceSessionService.createAttendanceSession(id));
    }

    @PostMapping("/roll-call")
    public JsonResponse<RollCallResponse> rollCall(@RequestBody RollCallRequest rollCallRequest) {
        return JsonResponse.success(attendanceSessionService.rollCall(rollCallRequest.getClassActivityId(),rollCallRequest.getAttendanceCode()));
    }

    @PutMapping()
    public JsonResponse<String> updateAttendanceSession(@RequestBody AttendanceRecordRequest request) {
        attendanceSessionService.updateAttendanceRecord(request);
        return JsonResponse.success("Update Attendance Session successfully!");
    }

    @GetMapping("/{id}")
    public JsonResponse<List<AttendanceRecordResponse>> getAttendanceSession(@PathVariable int id) {
        return JsonResponse.success(attendanceSessionService.getAttendanceRecords(id));
    }




}
