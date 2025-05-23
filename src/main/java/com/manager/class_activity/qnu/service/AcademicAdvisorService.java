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
import com.manager.class_activity.qnu.until.AcademicYearUtil;
import com.manager.class_activity.qnu.until.FileUtil;
import com.manager.class_activity.qnu.until.SecurityUtils;
import com.manager.class_activity.qnu.until.StringUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AcademicAdvisorService {

    private static final Logger log = LoggerFactory.getLogger(AcademicAdvisorService.class);
    AcademicAdvisorRepository academicAdvisorRepository;
    AcademicAdvisorMapper academicAdvisorMapper;
    LecturerService lecturerService;
    ClassService classService;
    AccountService accountService;

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
        if(!lecturer.getDepartment().equals(clazz.getDepartment())){
            throw new BadException(ErrorCode.DEPARTMENT_NOT_MATCH);
        }
        if(SecurityUtils.isRoleDepartment()){
            if(!lecturer.getDepartment().equals(accountService.getDepartmentOfAccount())){
                throw new BadException(ErrorCode.ACCESS_DENIED);
            }
        }

        AcademicAdvisor advisor = new AcademicAdvisor();
        advisor.setLecturer(lecturer);
        advisor.setClazz(clazz);
        advisor.setAcademicYear(StringUtils.removeAllSpaces(request.getAcademicYear()));
        if(academicAdvisorRepository
                .existsAcademicAdvisorByAcademicYearAndClazzAndLecturer(advisor.getAcademicYear()
                        , advisor.getClazz()
                        , advisor.getLecturer())){
            return;
        }

        academicAdvisorRepository.save(advisor);
    }

    @CacheEvict(value = "academicYears", allEntries = true)
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

    @CacheEvict(value = "academicYears", allEntries = true)
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
                request.getClassId(),
                request.getFilter().getAcademicYear()
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

    private AcademicAdvisorRequest parseFileRecord(Row row){
        try {
            return AcademicAdvisorRequest.builder()
                    .lecturerId((int)  Double.parseDouble(FileUtil.getCellValueAsString(row.getCell(0))))
                    .classId((int)  Double.parseDouble(FileUtil.getCellValueAsString(row.getCell(1))))
                    .academicYear(FileUtil.getCellValueAsString(row.getCell(2)))
                    .build();
        } catch (Exception e) {
            log.error("Error parsing File record: {}", row, e);
            throw new BadException(ErrorCode.INVALID_FORMAT_CSV);
        }
    }

    @CacheEvict(value = "academicYears", allEntries = true)
    public void saveAdvisors(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            boolean isHeader = true;

            for (Row row : sheet) {
                if (isHeader) { // Bỏ qua dòng tiêu đề
                    isHeader = false;
                    continue;
                }
                if ( ObjectUtils.isEmpty(row.getCell(0)) || ObjectUtils.isEmpty(row.getCell(1))  || ObjectUtils.isEmpty(row.getCell(2)) ) {
                    continue; // next if miss data
                }
                save(parseFileRecord(row));
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BadException(ErrorCode.INVALID_FORMAT_CSV);
        }
    }


    public Lecturer getAdvisorOfClass(Class clazz) {
        List<AcademicAdvisor> advisors = new ArrayList<>(academicAdvisorRepository
                .findByAcademicYearAndClazzAndIsDeletedOrderByUpdatedAt(
                        AcademicYearUtil.getCurrentAcademicYear()
                        , clazz
                        , false));
        if (advisors.isEmpty()){
            return null;
        }else {
            return advisors.getFirst().getLecturer();
        }
    }

    @Cacheable(value = "academicYears")
    public List<String> getAcademicYears() {
        System.out.println("LẤY TỪ DB nè!");
        return academicAdvisorRepository.getAcademicYears();
    }
}
