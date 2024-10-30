package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.dto.request.ClassRequest;
import com.manager.class_activity.qnu.dto.response.ClassResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.dto.response.SummaryClassResponse;
import com.manager.class_activity.qnu.entity.Class;
import com.manager.class_activity.qnu.entity.Department;
import com.manager.class_activity.qnu.entity.Course;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.mapper.ClassMapper;
import com.manager.class_activity.qnu.repository.ClassRepository;
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
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClassService {
    ClassRepository classRepository;
    ClassMapper classMapper;
    DepartmentService departmentService;
    CourseService courseService;

    public PagedResponse<ClassResponse> getClasses(CustomPageRequest<?> request) {
        Page<Class> classes = classRepository.getClassesByPaged(request.toPageable(), request.getKeyWord(), request.getDepartmentId(), request.getCourseId());
        List<ClassResponse> classResponses = new ArrayList<>();
        for (Class clazz : classes.getContent()) {
            classResponses.add(classMapper.toClassResponse(clazz));
        }
        return new PagedResponse<>(
                classResponses,
                classes.getNumber(),
                classes.getTotalElements(),
                classes.getTotalPages(),
                classes.isLast()
        );
    }

    public ClassResponse getClassResponseById(int classId) {
        return classMapper.toClassResponse(getClassById(classId));
    }

    public void saveClass(ClassRequest request) {
        Department department = departmentService.getDepartmentById(request.getDepartmentId());
        Course course = courseService.getCourseById(request.getCourseId());

        Class clazz = classMapper.toClass(request);
        clazz.setDepartment(department);
        clazz.setCourse(course);
        classRepository.save(clazz);
    }

    public void updateClass(int classId, ClassRequest request) {
        Class clazz = getClassById(classId);
        Department department = departmentService.getDepartmentById(request.getDepartmentId());
        Course course = courseService.getCourseById(request.getCourseId());

        classMapper.updateClass(clazz, request);
        clazz.setDepartment(department);
        clazz.setCourse(course);
        classRepository.save(clazz);
    }

    public Class getClassById(int classId) {
        return classRepository.findByIdAndIsDeleted(classId, false)
                .orElseThrow(() -> new BadException(ErrorCode.CLASS_NOT_FOUND));
    }

    public void deleteClass(int classId) {
        Class clazz = getClassById(classId);
        clazz.setDeleted(true);
        classRepository.save(clazz);
    }

    public void saveClasses(MultipartFile file) {
        try (CSVParser csvParser = new CSVParser(new InputStreamReader(file.getInputStream()), CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : csvParser) {
                if(hadClassName(record.get("name"))){
                    continue;
                }
                ClassRequest clazz = ClassRequest.builder()
                        .name(record.get("name"))
                        .departmentId(Integer.parseInt(record.get("department_id")))
                        .courseId(Integer.parseInt(record.get("course_id")))
                        .build();
                saveClass(clazz);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadException(ErrorCode.INVALID_FORMAT_CSV);
        }
    }

    private boolean hadClassName(String name) {
        return !ObjectUtils.isEmpty(classRepository.findByNameAndIsDeleted(name, false));
    }

    public List<SummaryClassResponse> getSummaryclass() {
        List<Class> classes = classRepository.getAllByIsDeleted(false);
        List<SummaryClassResponse> summaryClassResponses = new ArrayList<>();
        for (Class clazz : classes) {
            summaryClassResponses.add(classMapper.toSummaryClassResponse(clazz));
        }
        return summaryClassResponses;
    }

    public List<Class> getClasses(){
        float currentYear = (float) LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();
        if(currentMonth >=6){
            currentYear = (float) (currentYear + 0.5);
        }
        log.info("currentYear: " + currentYear);
        return classRepository.findByStartYearAndDurationYearsGreaterThan(currentYear);
    }
}
