package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.dto.request.LecturerRequest;
import com.manager.class_activity.qnu.dto.response.LecturerResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.entity.Account;
import com.manager.class_activity.qnu.entity.Department;
import com.manager.class_activity.qnu.entity.GenderEnum;
import com.manager.class_activity.qnu.entity.Lecturer;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.helper.StringHelper;
import com.manager.class_activity.qnu.mapper.LecturerMapper;
import com.manager.class_activity.qnu.repository.LecturerRepository;
import com.manager.class_activity.qnu.until.FileUtil;
import com.manager.class_activity.qnu.until.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
public class LecturerService {
    LecturerRepository lecturerRepository;
    LecturerMapper lecturerMapper;
    DepartmentService departmentService;
    AccountService accountService;
    TypeService typeService;

    @Transactional
    public void saveLecturers(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            boolean isHeader = true;

            for (Row row : sheet) {
                if (isHeader) { // Bỏ qua dòng tiêu đề
                    isHeader = false;
                    continue;
                }
                saveLecturer(parseFileRecord(row));
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BadException(ErrorCode.INVALID_FORMAT_CSV);
        }
    }

    private LecturerRequest parseFileRecord(Row row) {
        try{
            return LecturerRequest.builder()
                    .departmentId((int) Double.parseDouble(FileUtil.getCellValueAsString(row.getCell(6))))
                    .name(FileUtil.getCellValueAsString(row.getCell(0)))
                    .gender(GenderEnum.valueOf(StringHelper.processString(FileUtil.getCellValueAsString(row.getCell(1)))))
                    .birthDate(new SimpleDateFormat("MM/dd/yyyy").parse(FileUtil.getCellValueAsString(row.getCell(2))))
                    .email(FileUtil.getCellValueAsString(row.getCell(3)))
                    .phoneNumber(FileUtil.getCellValueAsString(row.getCell(4)))
                    .degree(FileUtil.getCellValueAsString(row.getCell(5)))
                    .build();
        }catch (ParseException e){
            log.error("Error parsing File record: {}", row, e);
            throw new BadException(ErrorCode.INVALID_FORMAT_CSV);
        }
    }

    public PagedResponse<LecturerResponse> getLecturers(CustomPageRequest<?> request) {
        Page<Lecturer> lecturers = lecturerRepository.getLecturersByPaged(request.toPageable(), request.getKeyWord(), request.getDepartmentId());
        List<LecturerResponse> lecturerResponses = new ArrayList<>();
        for (Lecturer lecturer : lecturers.getContent()) {
            lecturerResponses.add(lecturerMapper.toLecturerResponse(lecturer));
        }
        return new PagedResponse<>(
                lecturerResponses,
                lecturers.getNumber(),
                lecturers.getTotalElements(),
                lecturers.getTotalPages(),
                lecturers.isLast()
        );
    }

    public LecturerResponse getLecturerResponseById(int lecturerId) {
        return lecturerMapper.toLecturerResponse(getLecturerById(lecturerId));
    }

    public void updateLecturer(int lecturerId, LecturerRequest request) {
        Lecturer lecturer = getLecturerById(lecturerId);
        Department department = departmentService.getDepartmentById(request.getDepartmentId());
        lecturerMapper.updateLecturerFromRequest(request, lecturer);
        lecturer.setDepartment(department);
        lecturerRepository.save(lecturer);
    }

    public Lecturer getLecturerById(int lecturerId) {
        return lecturerRepository.findByIdAndIsDeleted(lecturerId, false)
                .orElseThrow(() -> new BadException(ErrorCode.LECTURER_NOT_FOUND));
    }

    public void deleteLecturer(int lecturerId) {
        Lecturer lecturer = getLecturerById(lecturerId);
        lecturer.setDeleted(true);
        accountService.deleteAccount(lecturer.getAccount().getId());
        lecturerRepository.save(lecturer);
    }

    public void saveLecturer(LecturerRequest request) {
        if (hadLecturerWithEmail(request.getEmail())) {
            throw new BadException(ErrorCode.LECTURER_IS_EXISTED);
        }
        Department department;
        if(SecurityUtils.isRoleDepartment()){
            department = accountService.getDepartmentOfAccount();
        }else {
            department = departmentService.getDepartmentById(request.getDepartmentId());
        }
        Lecturer lecturer = lecturerMapper.toLecturer(request);
        Account account = Account.builder()
                .username(request.getEmail())
                .type(typeService.getTypeDepartment())
                .password(StringHelper.createPassword(request.getBirthDate()))
                .build();
        log.info(account.getPassword());
        accountService.saveAccount(account);
        lecturer.setAccount(account);
        lecturer.setDepartment(department);
        lecturerRepository.save(lecturer);
    }

    public boolean hadLecturerWithEmail(String email) {
        return lecturerRepository.existsByEmailAndIsDeleted(email, false);
    }

    public List<LecturerResponse> getAll() {
        List<Lecturer> lecturers;
        if(SecurityUtils.isRoleDepartment()){
            lecturers = lecturerRepository.findAllByDepartment_IdAndIsDeleted(accountService.getDepartmentOfAccount().getId(), false);
        }else {
            lecturers = lecturerRepository.findAllByIsDeleted(false);
        }
        List<LecturerResponse> list = new ArrayList<>();
        for (Lecturer lec: lecturers) {
            list.add(lecturerMapper.toLecturerResponse(lec));
        }
        return list;
    }
}
