package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.dto.request.ClassRequest;
import com.manager.class_activity.qnu.dto.request.Filter;
import com.manager.class_activity.qnu.dto.response.*;
import com.manager.class_activity.qnu.entity.Class;
import com.manager.class_activity.qnu.entity.*;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.mapper.ClassMapper;
import com.manager.class_activity.qnu.mapper.StudentMapper;
import com.manager.class_activity.qnu.repository.*;
import com.manager.class_activity.qnu.until.AcademicYearUtil;
import com.manager.class_activity.qnu.until.FileUtil;
import com.manager.class_activity.qnu.until.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    StudentRepository studentRepository;
    StudentMapper studentMapper;
    StudentPositionRepository studentPositionRepository;
    AccountService accountService;
    AccountRepository accountRepository;
    AcademicAdvisorRepository academicAdvisorRepository;

    public PagedResponse<ClassResponse> getClasses(CustomPageRequest<?> request) {
        Page<Class> classes = classRepository.getClassesByPaged(request.toPageable(), request.getKeyWord(), request.getDepartmentId(), request.getCourseId());
        List<ClassResponse> classResponses = new ArrayList<>();
        for (Class clazz : classes.getContent()) {
            classResponses.add(classMapper.toClassResponse(clazz));
        }
        return new PagedResponse<>(classResponses, classes.getNumber(), classes.getTotalElements(), classes.getTotalPages(), classes.isLast());
    }

    public ClassResponse getClassResponseById(int classId) {
        return classMapper.toClassResponse(getClassById(classId));
    }

    public void saveClass(ClassRequest request) {
        Department department;
        if(SecurityUtils.isRoleDepartment()){
            department = accountService.getDepartmentOfAccount();
        }else {
            department = departmentService.getDepartmentById(request.getDepartmentId());
        }

        Course course = courseService.getCourseById(request.getCourseId());

        Class clazz = classMapper.toClass(request);
        if(request.getDurationYears()!=null) {
            clazz.setDurationYears(new BigDecimal(request.getDurationYears()));
        }
        else{
            clazz.setDurationYears(new BigDecimal(4));
        }
        clazz.setDepartment(department);
        clazz.setCourse(course);
        classRepository.save(clazz);
    }

    public void updateClass(int classId, ClassRequest request) {
        Class clazz = getClassById(classId);
        Department department = departmentService.getDepartmentById(request.getDepartmentId());
        Course course = courseService.getCourseById(request.getCourseId());

        classMapper.updateClass(clazz, request);
        clazz.setDurationYears(new BigDecimal(request.getDurationYears()));
        clazz.setDepartment(department);
        clazz.setCourse(course);
        classRepository.save(clazz);
    }

    public Class getClassById(int classId) {
        return classRepository.findByIdAndIsDeleted(classId, false).orElseThrow(() -> new BadException(ErrorCode.CLASS_NOT_FOUND));
    }

    public void deleteClass(int classId) {
        Class clazz = getClassById(classId);
        clazz.setDeleted(true);
        classRepository.save(clazz);
    }

    public void     saveClasses(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên

            boolean isHeader = true;
            for (Row row : sheet) {
                if (isHeader) { // Bỏ qua dòng tiêu đề
                    isHeader = false;
                    continue;
                }

                Cell departmentIdCell = row.getCell(0); // Cột "department_id"
                Cell nameCell = row.getCell(1); // Cột "name"
                Cell courseIdCell = row.getCell(2); // Cột "course_id"
                Cell durationYearsCell = row.getCell(3); // Cột "duration_years"

                if (departmentIdCell == null || nameCell == null || courseIdCell == null || durationYearsCell == null) {
                    continue; // Bỏ qua nếu thiếu dữ liệu
                }

                // Đọc dữ liệu từ các ô
                Integer deptId = (int) departmentIdCell.getNumericCellValue();
                String name = nameCell.getStringCellValue().trim();
                Integer courseId = (int) courseIdCell.getNumericCellValue();
                String durationYears = FileUtil.getCellValueAsString(durationYearsCell);

                // Kiểm tra nếu lớp học đã tồn tại
                if (hadClassName(name)) {
                    continue;
                }

                // Tạo ClassRequest và lưu lớp học
                ClassRequest clazz = ClassRequest.builder()
                        .name(name)
                        .departmentId(deptId)
                        .durationYears(durationYears)
                        .courseId(courseId)
                        .build();
                saveClass(clazz);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadException(ErrorCode.INVALID_FORMAT_CSV); // Đổi thông báo lỗi cho Excel
        }
    }

    private boolean hadClassName(String name) {
        return !ObjectUtils.isEmpty(classRepository.findByNameAndIsDeleted(name, false));
    }

    public List<SummaryClassResponse> getSummaryclass() {
        List<Class> classes;
        if(SecurityUtils.isRoleDepartment()){
            classes = classRepository.findAllByDepartment_IdAndIsDeleted(accountService.getDepartmentOfAccount().getId(), false);
        }else {
            classes = classRepository.getAllByIsDeleted(false);
        }
        List<SummaryClassResponse> summaryClassResponses = new ArrayList<>();
        for (Class clazz : classes) {
            summaryClassResponses.add(classMapper.toSummaryClassResponse(clazz));
        }
        return summaryClassResponses;
    }

    public List<Class> getClasses() {
        float currentYear = (float) LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();
        if (currentMonth >= 6) {
            currentYear = (float) (currentYear + 0.5);
        }
        return classRepository.findByStartYearAndDurationYearsGreaterThan(currentYear);
    }

    public ClassDetailResponse getClassDetailById(int classId, CustomPageRequest<?> request) {
        ClassDetailResponse result = new ClassDetailResponse();
        Class clazz = getClassById(classId);
        Page<Student> students = studentRepository.getStudentsByPaged(request.toPageable(), request.getKeyWord(), null, null, classId);
        List<StudentOfClass> studentOfClassList = new ArrayList<>();
        result.setName(clazz.getName());
        for (Student s : students.getContent()) {
            studentOfClassList.add(studentMapper.toStudentOfClass(s));
        }
        for (StudentOfClass studentOfClass : studentOfClassList) {
            studentOfClass.setStudentPosition(studentPositionRepository.findLatestActivePositionByStudentId(studentOfClass.getId()).getPosition().toString());
        }
        result.setStudentOfClass(studentOfClassList);
        result.setTotal(students.getTotalElements());
        result.setId(classId);
        return result;
    }

    public ClassDetailResponse getMyClass(CustomPageRequest<Filter> request) {
        if("STUDENT".equals(SecurityUtils.getCurrentUserType())) {
            Student student = accountRepository.getStudentByUsername(SecurityUtils.getCurrentUsername());
            return getClassDetailById(student.getClazz().getId(),request);
        }
        throw new BadException(ErrorCode.ACCESS_DENIED);
    }

    public List<ClassResponse> getMyClassLecturer() {
        if("DEPARTMENT".equals((SecurityUtils.getCurrentUserType()))){
            List<ClassResponse> classResponses = new ArrayList<>();
            Lecturer lecturer = accountRepository.getLecturerByUsername(SecurityUtils.getCurrentUsername());
            for (AcademicAdvisor item: academicAdvisorRepository.findByAcademicYearAndLecturerAndIsDeletedOrderByUpdatedAt(AcademicYearUtil.getCurrentAcademicYear(),lecturer,false)) {
                classResponses.add(classMapper.toClassResponse(item.getClazz()));
            }
            return classResponses;
        }
        throw new BadException(ErrorCode.ACCESS_DENIED);
    }
}
