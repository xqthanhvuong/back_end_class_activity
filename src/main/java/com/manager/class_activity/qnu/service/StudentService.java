package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.dto.request.ClassRequest;
import com.manager.class_activity.qnu.dto.request.StudentRequest;
import com.manager.class_activity.qnu.dto.response.StudentResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.entity.GenderEnum;
import com.manager.class_activity.qnu.entity.Student;
import com.manager.class_activity.qnu.entity.Class;
import com.manager.class_activity.qnu.entity.Account;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.helper.StringHelper;
import com.manager.class_activity.qnu.mapper.StudentMapper;
import com.manager.class_activity.qnu.repository.StudentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class StudentService {
    StudentRepository studentRepository;
    StudentMapper studentMapper;
    ClassService classService;
    AccountService accountService;
    TypeService typeService;

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
            studentResponses.add(studentMapper.toStudentResponse(student));
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
        return studentMapper.toStudentResponse(getStudentById(studentId));
    }

    public void saveStudent(StudentRequest request) {
        Class clazz = classService.getClassById(request.getClassId());
        Account account = Account.builder()
                .username(request.getStudentCode())
                .password(StringHelper.createPassword(request.getBirthDate()))
                .type(typeService.getTypeStudent())
                .build();
        accountService.saveAccount(account);
        Student student = studentMapper.toStudent(request);
        student.setClazz(clazz);
        student.setAccount(account);
        studentRepository.save(student);
    }

    public void updateStudent(int studentId, StudentRequest request) {
        Student student = getStudentById(studentId);
        Class clazz = classService.getClassById(request.getClassId());

        studentMapper.updateStudent(student, request);
        student.setClazz(clazz);
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

    public void saveStudents(MultipartFile file) {
        try (CSVParser csvParser = new CSVParser(new InputStreamReader(file.getInputStream()), CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : csvParser) {
                if (hadStudentCode(record.get("student_code"))) {
                    continue;
                }
                StudentRequest student = StudentRequest.builder()
                        .name(record.get("name"))
                        .classId(Integer.parseInt(record.get("class_id")))
                        .studentCode(record.get("student_code"))
                        .email(record.get("email"))
                        .birthDate(new SimpleDateFormat("MM/dd/yyyy").parse(record.get("birth_date")))
                        .gender(GenderEnum.valueOf(StringHelper.processString(record.get("gender"))))
                        .build();
                saveStudent(student);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadException(ErrorCode.INVALID_FORMAT_CSV);
        }
    }

    private boolean hadStudentCode(String studentCode) {
        return !ObjectUtils.isEmpty(studentRepository.findByStudentCodeAndIsDeleted(studentCode, false));
    }
}