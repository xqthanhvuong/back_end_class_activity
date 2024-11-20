package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.dto.response.ClassActivityResponse;
import com.manager.class_activity.qnu.dto.response.ClassResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.entity.*;
import com.manager.class_activity.qnu.entity.Class;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.mapper.ClassActivityMapper;
import com.manager.class_activity.qnu.repository.ClassActivityRepository;
import com.manager.class_activity.qnu.repository.StudentPositionRepository;
import com.manager.class_activity.qnu.until.DateTimeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClassActivityService {
    ClassActivityRepository classActivityRepository;
    ClassService classService;
    ActivityGuideService activityGuideService;
    StudentPositionRepository studentPositionRepository;
    ClassActivityMapper classActivityMapper;

    @Transactional(rollbackFor = Exception.class)
    public void createAllClassActivity(MultipartFile file) {
        // Tạo ActivityGuide từ file
        ActivityGuide activityGuide = activityGuideService.createActivityGuide(file);
        for (Class clazz : classService.getClasses()) {
            StudentPosition position = studentPositionRepository
                    .findStudentPositionByClassIdAndPosition(clazz.getId(), StudentPositionEnum.ClassLeader);

            Student lead = (position != null) ? position.getStudent() : null;

            // Tạo ClassActivity
            ClassActivity classActivity = ClassActivity.builder()
                    .activityGuide(activityGuide)
                    .leader(lead)
                    .clazz(clazz)
                    .activityTime(Timestamp.valueOf(DateTimeUtils.getLastFridayOfCurrentMonthAt10AM()))
                    .build();

            // Lưu ClassActivity vào cơ sở dữ liệu
            classActivityRepository.save(classActivity);
        }
    }

    public PagedResponse<ClassActivityResponse> getClassActivities(CustomPageRequest<?> request) {
        Page<ClassActivity> classActivityPage = classActivityRepository.getClassActivitiesByPaged(
                request.toPageable(),
                request.getKeyWord(),
                request.getDepartmentId(),
                request.getCourseId(),
                request.getClassId()
        );
        List<ClassActivityResponse> classActivityResponses = new ArrayList<>();
        for(ClassActivity classActivity : classActivityPage.getContent()) {
            classActivityResponses.add(classActivityMapper.toClassActivityResponse(classActivity));
        }
        return new PagedResponse<>(
                classActivityResponses,
                classActivityPage.getNumber(),
                classActivityPage.getTotalElements(),
                classActivityPage.getTotalPages(),
                classActivityPage.isLast());
    }

}
