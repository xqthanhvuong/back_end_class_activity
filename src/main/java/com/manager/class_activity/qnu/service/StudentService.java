package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.dto.request.StudentPositionRequest;
import com.manager.class_activity.qnu.dto.request.StudentRequest;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.dto.response.StudentResponse;
import com.manager.class_activity.qnu.entity.Class;
import com.manager.class_activity.qnu.entity.*;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.helper.StringHelper;
import com.manager.class_activity.qnu.mapper.StudentMapper;
import com.manager.class_activity.qnu.repository.AccountRoleRepository;
import com.manager.class_activity.qnu.repository.RoleRepository;
import com.manager.class_activity.qnu.repository.StudentPositionRepository;
import com.manager.class_activity.qnu.repository.StudentRepository;
import com.manager.class_activity.qnu.until.FileUtil;
import com.manager.class_activity.qnu.until.SecurityUtils;
import jakarta.transaction.Transactional;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StudentService {
    StudentRepository studentRepository;
    StudentMapper studentMapper;
    ClassService classService;
    AccountService accountService;
    AccountRoleRepository accountRoleRepository;
    TypeService typeService;
    StudentPositionService studentPositionService;
    StudentPositionRepository studentPositionRepository;
    RoleRepository roleRepository;

    public PagedResponse<StudentResponse> getStudents(CustomPageRequest<?> request) {
        Page<Student> students = studentRepository.getStudentsByPaged(
                request.toPageable(),
                request.getKeyWord(),
                request.getDepartmentId(),
                request.getCourseId(),
                request.getClassId()
        );
        List<StudentResponse> studentResponses = new ArrayList<>();
        for (Student student : students.getContent()) {
            StudentResponse studentResponse = studentMapper.toStudentResponse(student);
            StudentPosition studentPosition = studentPositionRepository.findLatestActivePositionByStudentId(student.getId());
            if(ObjectUtils.isNotEmpty(studentPosition)) {
                studentResponse.setStudentPosition(studentPosition.getPosition().toString());
            }
            studentResponses.add(studentResponse);
        }
        return new PagedResponse<>(
                studentResponses,
                students.getNumber(),
                students.getTotalElements(),
                students.getTotalPages(),
                students.isLast()
        );
    }

    public StudentResponse getStudentResponseById(int studentId) {
        StudentResponse studentResponse = studentMapper.toStudentResponse(getStudentById(studentId));
        StudentPosition studentPosition = studentPositionRepository.findLatestActivePositionByStudentId(studentResponse.getId());
        if(ObjectUtils.isNotEmpty(studentPosition)) {
            studentResponse.setStudentPosition(studentPosition.getPosition().toString());
        }
        return studentResponse;
    }

    @Transactional
    public void saveStudent(StudentRequest request) {
        Class clazz = classService.getClassById(request.getClassId());
        if(SecurityUtils.isRoleDepartment()){
            if(!clazz.getDepartment().equals(accountService.getDepartmentOfAccount())){
                throw new BadException(ErrorCode.ACCESS_DENIED);
            }
        }
        Account account = Account.builder()
                .username(request.getStudentCode())
                .password(StringHelper.createPassword(request.getBirthDate()))
                .type(typeService.getTypeStudent())
                .build();
        AccountRole accountRole = AccountRole.builder()
                .role(roleRepository.findByIdAndIsDeleted(4,false).orElseThrow(
                                ()->new BadException(ErrorCode.ROLE_NOT_FOND)
                ))
                .account(account)
                .build();
        accountService.saveAccount(account);
        accountRoleRepository.save(accountRole);
        Student student = studentMapper.toStudent(request);
        student.setClazz(clazz);
        student.setAccount(account);
        studentRepository.save(student);
        StudentPositionRequest studentPositionRequest = StudentPositionRequest.builder()
                .studentId(student.getId())
                .position(ObjectUtils.isNotEmpty(request.getStudentPositionEnum())
                        ? request.getStudentPositionEnum() : StudentPositionEnum.Member)
                .classId(clazz.getId())
                .build();
        studentPositionService.saveStudentPosition(studentPositionRequest);
    }

    @Transactional
    public void updateStudent(int studentId, StudentRequest request) {
        Student student = getStudentById(studentId);
        Class clazz = classService.getClassById(request.getClassId());
        studentMapper.updateStudent(student, request);
        student.setClazz(clazz);
        StudentPositionRequest studentPositionRequest = StudentPositionRequest.builder()
                .classId(clazz.getId())
                .studentId(studentId)
                .position(request.getStudentPositionEnum())
                .build();
        studentPositionService.saveStudentPosition(studentPositionRequest);
        studentRepository.save(student);
    }

    public Student getStudentById(int studentId) {
        return studentRepository.findByIdAndIsDeleted(studentId, false)
                .orElseThrow(() -> new BadException(ErrorCode.STUDENT_NOT_FOUND));
    }

    public void deleteStudent(int studentId) {
        Student student = getStudentById(studentId);
        student.setDeleted(true);
        accountService.deleteAccount(student.getAccount().getId());
        studentRepository.save(student);
    }

    @Transactional
    public void saveStudents(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean isHeader = true;
            for (Row row : sheet) {
                if(isHeader){
                    isHeader = false;
                    continue;
                }
                saveStudent(parseFileRecord(row));
            }

        }catch (IOException e) {
            log.error(e.getMessage());
            throw new BadException(ErrorCode.INVALID_FORMAT_CSV);
        }
    }

    private StudentRequest parseFileRecord(Row row){
        try {
            return StudentRequest.builder()
                    .studentCode(FileUtil.getCellValueAsString(row.getCell(0)))
                    .name(FileUtil.getCellValueAsString(row.getCell(1)))
                    .gender(GenderEnum.valueOf(StringHelper.processString(FileUtil.getCellValueAsString(row.getCell(2)))))
                    .birthDate(new SimpleDateFormat("MM/dd/yyyy").parse(FileUtil.getCellValueAsString(row.getCell(3))))
                    .email(FileUtil.getCellValueAsString(row.getCell(4)))
                    .studentPositionEnum(StudentPositionEnum.valueOf(StringHelper.removeAllWhitespace(StringHelper.processString(FileUtil.getCellValueAsString(row.getCell(5))))))
                    .classId(Integer.parseInt(FileUtil.getCellValueAsString(row.getCell(6))))
                    .build();
        }catch (ParseException e){
            log.error(e.getMessage());
            throw new BadException(ErrorCode.INVALID_FORMAT_CSV);
        }
    }

    private boolean hadStudentCode(String studentCode) {
        return !ObjectUtils.isEmpty(studentRepository.findByStudentCodeAndIsDeleted(studentCode, false));
    }
}
