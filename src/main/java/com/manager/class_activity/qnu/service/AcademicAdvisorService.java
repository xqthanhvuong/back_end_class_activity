package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.dto.request.AcademicAdvisorRequest;
import com.manager.class_activity.qnu.dto.request.FilterAdvisor;
import com.manager.class_activity.qnu.dto.response.AcademicAdvisorResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.entity.AcademicAdvisor;
import com.manager.class_activity.qnu.entity.Class;
import com.manager.class_activity.qnu.entity.Lecturer;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.mapper.AcademicAdvisorMapper;
import com.manager.class_activity.qnu.repository.AcademicAdvisorRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcademicAdvisorService {

    private static final Logger log = LoggerFactory.getLogger(AcademicAdvisorService.class);
    private final AcademicAdvisorRepository academicAdvisorRepository;
    private final AcademicAdvisorMapper academicAdvisorMapper;
    private final LecturerService lecturerService;
    private final ClassService classService;

    public List<AcademicAdvisorResponse> getAll() {
        List<AcademicAdvisor> advisors = academicAdvisorRepository.findAllByIsDeletedFalse();
        return advisors.stream()
                .map(academicAdvisorMapper::toResponse)
                .collect(Collectors.toList());
    }

    public AcademicAdvisorResponse getById(int id) {
        AcademicAdvisor advisor = academicAdvisorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BadException(ErrorCode.ADVISOR_NOT_FOUND));
        return academicAdvisorMapper.toResponse(advisor);
    }

    public void save(AcademicAdvisorRequest request) {
        Lecturer lecturer = lecturerService.getLecturerById(request.getLecturerId());
        Class clazz = classService.getClassById(request.getClassId());

        AcademicAdvisor advisor = new AcademicAdvisor();
        advisor.setLecturer(lecturer);
        advisor.setClazz(clazz);
        advisor.setAcademicYear(request.getAcademicYear());

        academicAdvisorRepository.save(advisor);
    }

    public void update(int id, AcademicAdvisorRequest request) {
        AcademicAdvisor advisor = academicAdvisorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BadException(ErrorCode.ADVISOR_NOT_FOUND));

        Lecturer lecturer = lecturerService.getLecturerById(request.getLecturerId());
        Class clazz = classService.getClassById(request.getClassId());

        advisor.setLecturer(lecturer);
        advisor.setClazz(clazz);
        advisor.setAcademicYear(request.getAcademicYear());

        academicAdvisorRepository.save(advisor);
    }

    public void delete(int id) {
        AcademicAdvisor advisor = academicAdvisorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BadException(ErrorCode.ADVISOR_NOT_FOUND));
        advisor.setDeleted(true);
        academicAdvisorRepository.save(advisor);
    }

    public PagedResponse<AcademicAdvisorResponse> getAdvisor(CustomPageRequest<FilterAdvisor> request) {
        Page<AcademicAdvisor> advisors = academicAdvisorRepository.getAdvisorsByPaged(
                request.toPageable(),
                request.getKeyWord(),
                request.getDepartmentId(),
                request.getCourseId(),
                request.getClassId()
        );
        List<AcademicAdvisorResponse> advisorResponses = new ArrayList<>();
        for (AcademicAdvisor advisor : advisors) {
            advisorResponses.add(academicAdvisorMapper.toResponse(advisor));
        }
        return new PagedResponse<>(
                advisorResponses,
                advisors.getNumber(),
                advisors.getTotalElements(),
                advisors.getTotalPages(),
                advisors.isLast()
        );
    }


    public void saveAdvisors(MultipartFile file) {
        try (CSVParser csvParser = new CSVParser(new InputStreamReader(file.getInputStream()), CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : csvParser) {
                AcademicAdvisorRequest academicAdvisorRequest = AcademicAdvisorRequest.builder()
                        .lecturerId(Integer.parseInt(record.get("lecturer_id")))
                        .classId(Integer.parseInt(record.get("class_id")))
                        .academicYear(record.get("academic_year"))
                        .build();
                save(academicAdvisorRequest);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadException(ErrorCode.INVALID_FORMAT_CSV);
        }
    }

    public Lecturer getAdvisorOfClass(int classId) {
        return academicAdvisorRepository.findTopByClazzIdAndIsDeletedOrderByCreatedAtDesc(classId,false)
                .getLecturer();
    }

}
