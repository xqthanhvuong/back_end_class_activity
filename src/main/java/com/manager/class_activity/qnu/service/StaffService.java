package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.dto.request.StaffRequest;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.dto.response.StaffResponse;
import com.manager.class_activity.qnu.entity.Department;
import com.manager.class_activity.qnu.entity.Staff;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.mapper.StaffMapper;
import com.manager.class_activity.qnu.repository.StaffRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StaffService {
    StaffRepository staffRepository;
    StaffMapper staffMapper;
    DepartmentService departmentService;

    public PagedResponse<StaffResponse> getStaffs(CustomPageRequest<?> request) {
        Page<Staff> staffs = staffRepository.getStaffsByPaged(request.toPageable(), request.getKeyWord());
        List<StaffResponse> staffResponses = new ArrayList<>();
        for (Staff staff : staffs.getContent()) {
            staffResponses.add(staffMapper.toStaffResponse(staff));
        }
        return new PagedResponse<>(
                staffResponses,
                staffs.getNumber(),
                staffs.getTotalElements(),
                staffs.getTotalPages(),
                staffs.isLast()
        );
    }

    public void updateStaff(int staffId, StaffRequest request) {
        Staff staff = getStaffById(staffId);
        Department department = departmentService.getDepartmentById(request.getDepartmentId());
        staffMapper.updateStaffFromRequest(request, staff);
        staff.setDepartment(department);
        staffRepository.save(staff);
    }

    public Staff getStaffById(int staffId) {
        return staffRepository.findByIdAndIsDeleted(staffId, false)
                .orElseThrow(() -> new BadException(ErrorCode.STAFF_NOT_FOND));
    }
    public void deleteStaff(int staffId) {
        Staff staff = getStaffById(staffId);
        staff.setDeleted(true);
        staffRepository.save(staff);
    }
    public void saveStaff(StaffRequest request) {
        Staff staff = staffMapper.toStaff(request);
    }
}
