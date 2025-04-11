package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.dto.request.CourseRequest;
import com.manager.class_activity.qnu.dto.response.CourseResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.dto.response.SummaryCourseResponse;
import com.manager.class_activity.qnu.entity.Course;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.mapper.CourseMapper;
import com.manager.class_activity.qnu.repository.CourseRepository;
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

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class CourseService {
    CourseRepository courseRepository;
    CourseMapper courseMapper;

    public void saveCourses(MultipartFile file) {
        try(Workbook workbook = new XSSFWorkbook(file.getInputStream())){
            Sheet sheet = workbook.getSheetAt(0);
            boolean isHeader = true;
            for(Row row : sheet){
                if(isHeader){
                    isHeader = false;
                    continue;
                }
                Cell nameCell = row.getCell(0); //"name"
                Cell startYearCell = row.getCell(1); //"start_year"
                Cell endYearCell = row.getCell(2); //"end_year"
                if(ObjectUtils.isEmpty(nameCell) || ObjectUtils.isEmpty(startYearCell) || ObjectUtils.isEmpty(endYearCell)){
                    continue;
                }
                String courseName = nameCell.getStringCellValue().trim();
                String startYear = startYearCell.getStringCellValue().trim();
                String endYear = endYearCell.getStringCellValue().trim();
                if(!isFourDigitNumber(startYear) || !isFourDigitNumber(endYear)){
                    continue;
                }
                if(hadCourseName(courseName)){
                    continue;
                }
                Course course = Course.builder()
                        .name(courseName)
                        .startYear(Integer.parseInt(startYear))
                        .endYear(Integer.parseInt(endYear))
                        .build();
                courseRepository.save(course);
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    // Hàm kiểm tra một chuỗi có phải số nguyên hay không
    private boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Lấy danh sách các Course với phân trang
    public PagedResponse<CourseResponse> getCourses(CustomPageRequest<?> request) {
        Page<Course> courses = courseRepository.getCoursesByPaged(request.toPageable()
        , request.getKeyWord());
        List<CourseResponse> courseResponses = new ArrayList<>();
        for (Course course : courses.getContent()) {
            courseResponses.add(courseMapper.toCourseResponse(course));
        }
        return new PagedResponse<>(
                courseResponses,
                courses.getNumber(),
                courses.getTotalElements(),
                courses.getTotalPages(),
                courses.isLast()
        );
    }

    // Lấy Course theo ID
    public CourseResponse getCourseResponseById(int id) {
        return courseMapper.toCourseResponse(getCourseById(id));
    }

    // Cập nhật Course
    public void updateCourse(int courseId, CourseRequest request) {
        Course course = getCourseById(courseId);
        courseMapper.updateCourse(course, request);
        courseRepository.save(course);
    }

    // Tạo mới Course
    public void saveCourse(CourseRequest request) {
        if(hadCourseName(request.getName())) {
            throw new BadException(ErrorCode.COURSE_NAME_EXISTED);
        }
        Course course = courseMapper.toCourse(request);
        courseRepository.save(course);
    }

    // Xóa Course
    public void deleteCourse(int id) {
        Course course = getCourseById(id);
        course.setDeleted(true);
        courseRepository.save(course);
    }

    public List<SummaryCourseResponse> getSummaryCourses(){
        List<Course> courses = courseRepository.getAllByIsDeleted(false);
        List<SummaryCourseResponse> summaryCourseResponses = new ArrayList<>();
        for (Course course : courses) {
            summaryCourseResponses.add(courseMapper.toSummaryCourse(course));
        }
        return summaryCourseResponses;
    }

    public Course getCourseById(int id){
        return courseRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new BadException(ErrorCode.COURSE_NOT_FOUND));
    }

    public boolean hadCourseName(String name){
        return !ObjectUtils.isEmpty(courseRepository.findByNameAndIsDeleted(name, false));
    }

    public static boolean isFourDigitNumber(String str) {
        return str.matches("\\d{4}");
    }
}
