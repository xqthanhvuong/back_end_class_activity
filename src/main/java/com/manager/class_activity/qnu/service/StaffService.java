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
import com.manager.class_activity.qnu.until.FileUtil;
import com.manager.class_activity.qnu.until.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StaffService {
    StaffRepository staffRepository;
    StaffMapper staffMapper;
    DepartmentService departmentService;
    AccountService accountService;
    TypeService typeService;

    @Transactional
    public void saveStaffs(MultipartFile file) {
        try(Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean isHeader = true;
            for(Row row : sheet) {
                if(isHeader){
                    isHeader = false;
                    continue;
                }
                saveStaff(parseFileRecord(row));
            }

        }catch (IOException e){
            log.error(e.getMessage());
            throw new BadException(ErrorCode.INVALID_FORMAT_CSV);
        }
    }

    public StaffRequest parseFileRecord(Row row){
        try {
            return StaffRequest.builder()
                    .name(FileUtil.getCellValueAsString(row.getCell(0)))
                    .gender(GenderEnum.valueOf(StringHelper.processString(FileUtil.getCellValueAsString(row.getCell(1)))))
                    .birthDate(new SimpleDateFormat("MM/dd/yyyy").parse(FileUtil.getCellValueAsString(row.getCell(2))))
                    .email(FileUtil.getCellValueAsString(row.getCell(3)))
                    .phoneNumber(FileUtil.getCellValueAsString(row.getCell(4)))
                    .departmentId(Integer.parseInt(FileUtil.getCellValueAsString(row.getCell(5))))
                    .build();
        }catch (ParseException e){
            log.error(e.getMessage());
            throw new BadException(ErrorCode.INVALID_FORMAT_CSV);
        }
    }

    public PagedResponse<StaffResponse> getStaffs(CustomPageRequest<?> request) {
        Page<Staff> staffs = staffRepository.getStaffsByPaged(request.toPageable(), request.getKeyWord(), request.getDepartmentId());
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
        Department department;
        if(SecurityUtils.isRoleDepartment()){
            department = accountService.getDepartmentOfAccount();
        }else {
            department = departmentService.getDepartmentById(request.getDepartmentId());
        }
        Staff staff = staffMapper.toStaff(request);
        Account account = Account.builder()
                .username(request.getEmail())
                .password(StringHelper.createPassword(request.getBirthDate()))
                .type(typeService.getTypeDepartment())
                .build();
        accountService.saveAccount(account);
        staff.setAccount(account);
        staff.setDepartment(department);
        staffRepository.save(staff);
    }

    public boolean hadStaffWithEmail(String email) {
        return !ObjectUtils.isEmpty(staffRepository.findByEmailAndIsDeleted(email, false));
    }
}
