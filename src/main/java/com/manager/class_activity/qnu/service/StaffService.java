package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.dto.request.StaffRequest;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.dto.response.StaffResponse;
import com.manager.class_activity.qnu.entity.Account;
import com.manager.class_activity.qnu.entity.Department;
import com.manager.class_activity.qnu.entity.GenderEnum;
import com.manager.class_activity.qnu.entity.Staff;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.helper.StringHelper;
import com.manager.class_activity.qnu.mapper.StaffMapper;
import com.manager.class_activity.qnu.repository.StaffRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StaffService {
    StaffRepository staffRepository;
    StaffMapper staffMapper;
    DepartmentService departmentService;
    AccountService accountService;
    TypeService typeService;

    public void saveStaffs(MultipartFile file) {
        try (CSVParser csvParser = new CSVParser(new InputStreamReader(file.getInputStream()), CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : csvParser) {

                StaffRequest staff = StaffRequest.builder()
                        .departmentId(Integer.parseInt(record.get("department_id")))
                        .email(record.get("email"))
                        .name(record.get("name"))
                        .birthDate(new SimpleDateFormat("yyyy-MM-dd").parse(record.get("birth_date")))
                        .phoneNumber(record.get("phone_number"))
                        .gender(GenderEnum.valueOf(StringHelper.processString(record.get("gender"))))
                        .build();
                System.out.println(staff.getDepartmentId());
                if(hadStaffWithEmail(record.get("email"))){
                    continue;
                }
                saveStaff(staff);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new BadException(ErrorCode.INVALID_FORMAT_CSV);
        }
    }

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

    public StaffResponse getStaffResponseById(int staffId) {
        return staffMapper.toStaffResponse(getStaffById(staffId));
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
        accountService.deleteAccount(staff.getAccount().getId());
        staffRepository.save(staff);
    }

    public void saveStaff(StaffRequest request) {
        if(hadStaffWithEmail(request.getEmail())){
            throw new BadException(ErrorCode.STAFF_IS_EXISTED);
        }
        Department department = departmentService.getDepartmentById(request.getDepartmentId());
        Staff staff = staffMapper.toStaff(request);
        Account account = Account.builder()
                .username(request.getEmail())
                .password(request.getBirthDate().toString())
                .type(typeService.getTypeDepartment())
                .build();
        System.out.println(request.getBirthDate().toString());
        accountService.saveAccount(account);
        staff.setAccount(account);
        staff.setDepartment(department);
        staffRepository.save(staff);
    }

    public boolean hadStaffWithEmail(String email) {
        return !ObjectUtils.isEmpty(staffRepository.findByEmailAndIsDeleted(email, false));
    }
}
