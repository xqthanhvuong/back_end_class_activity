package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.dto.request.LecturerRequest;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.dto.response.LecturerResponse;
import com.manager.class_activity.qnu.entity.Account;
import com.manager.class_activity.qnu.entity.Department;
import com.manager.class_activity.qnu.entity.GenderEnum;
import com.manager.class_activity.qnu.entity.Lecturer;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.mapper.LecturerMapper;
import com.manager.class_activity.qnu.repository.LecturerRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
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
public class LecturerService {
    LecturerRepository lecturerRepository;
    LecturerMapper lecturerMapper;
    DepartmentService departmentService;
    AccountService accountService;

    public void saveLecturers(MultipartFile file) {
        try (CSVParser csvParser = new CSVParser(new InputStreamReader(file.getInputStream()), CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : csvParser) {

                LecturerRequest lecturer = LecturerRequest.builder()
                        .departmentId(Integer.parseInt(record.get("department_id")))
                        .email(record.get("email"))
                        .name(record.get("name"))
                        .birthDate(new SimpleDateFormat("yyyy-MM-dd").parse(record.get("birth_date")))
                        .phoneNumber(record.get("phone_number"))
                        .gender(GenderEnum.valueOf(record.get("gender")))
                        .degree(record.get("degree"))
                        .build();
                if (hadLecturerWithEmail(record.get("email"))) {
                    continue;
                }
                saveLecturer(lecturer);
            }
        } catch (Exception e) {
            throw new BadException(ErrorCode.INVALID_FORMAT_CSV);
        }
    }

    public PagedResponse<LecturerResponse> getLecturers(CustomPageRequest<?> request) {
        Page<Lecturer> lecturers = lecturerRepository.getLecturersByPaged(request.toPageable(), request.getKeyWord());
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
        Department department = departmentService.getDepartmentById(request.getDepartmentId());
        Lecturer lecturer = lecturerMapper.toLecturer(request);
        Account account = Account.builder()
                .username(request.getEmail())
                .password(request.getBirthDate().toString())
                .build();
        accountService.saveAccount(account);
        lecturer.setAccount(account);
        lecturer.setDepartment(department);
        lecturerRepository.save(lecturer);
    }

    public boolean hadLecturerWithEmail(String email) {
        return lecturerRepository.existsByEmailAndIsDeleted(email, false);
    }
}
