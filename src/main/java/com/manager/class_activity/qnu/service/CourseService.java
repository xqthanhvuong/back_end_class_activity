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
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class CourseService {
    CourseRepository courseRepository;
    CourseMapper courseMapper;


    public void saveCourses(MultipartFile file) {
        try (CSVParser csvParser = new CSVParser(new InputStreamReader(file.getInputStream()), CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            // Kiểm tra xem các header có đúng không
            List<String> expectedHeaders = Arrays.asList("name", "start_year", "end_year");
            List<String> actualHeaders = csvParser.getHeaderNames();

            if (!actualHeaders.containsAll(expectedHeaders)) {
                throw new BadException(ErrorCode.INVALID_FORMAT_CSV);
            }

            for (CSVRecord record : csvParser) {
                // Kiểm tra từng giá trị
                String name = record.get("name");
                String startYear = record.get("start_year");
                String endYear = record.get("end_year");

                if (name == null || name.isEmpty()) {
                    throw new BadException(ErrorCode.INVALID_FORMAT_CSV);
                }
                if (!isInteger(startYear) || !isInteger(endYear)) {
                    throw new BadException(ErrorCode.INVALID_FORMAT_CSV);
                }
                if(hadCourseName(name)){
                    continue;
                }
                // Nếu hợp lệ thì lưu dữ liệu
                Course course = new Course();
                course.setName(name);
                course.setStartYear(Integer.parseInt(startYear));
                course.setEndYear(Integer.parseInt(endYear));
                courseRepository.save(course);
            }
        } catch (BadException e) {
            throw e;
        } catch (Exception e){
            throw new BadException(ErrorCode.UNCATEGORIZED_EXCEPTION);
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
}
