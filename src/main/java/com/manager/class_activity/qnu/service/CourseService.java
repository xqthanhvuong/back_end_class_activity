package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.dto.request.CourseRequest;
import com.manager.class_activity.qnu.dto.response.CourseResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class CourseService {
    CourseRepository courseRepository;
    CourseMapper courseMapper;

    // Tải file CSV và lưu các Course
    public void saveCourses(MultipartFile file) {
        try (CSVParser csvParser = new CSVParser(new InputStreamReader(file.getInputStream()), CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : csvParser) {
                Course course = new Course();
                course.setName(record.get("name"));
                course.setStartYear(Integer.parseInt(record.get("start_year")));
                course.setEndYear(Integer.parseInt(record.get("end_year")));
                courseRepository.save(course);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process CSV file: " + e.getMessage());
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
        return courseMapper.toCourseResponse(courseRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new BadException(ErrorCode.COURSE_NOT_FOUND)));
    }

    // Cập nhật Course
    public void updateCourse(int courseId, CourseRequest request) {
        Course course = courseRepository.findByIdAndIsDeleted(courseId, false)
                .orElseThrow(() -> new BadException(ErrorCode.COURSE_NOT_FOUND));
        courseMapper.updateCourse(course, request);
        courseRepository.save(course);
    }

    // Tạo mới Course
    public void saveCourse(CourseRequest request) {
        Course course = courseMapper.toCourse(request);
        courseRepository.save(course);
    }

    // Xóa Course
    public void deleteCourse(int id) {
        Course course = courseRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new BadException(ErrorCode.COURSE_NOT_FOUND));
        course.setDeleted(true);
        courseRepository.save(course);
    }
}