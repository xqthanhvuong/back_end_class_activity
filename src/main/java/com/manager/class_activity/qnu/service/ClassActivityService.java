package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.dto.response.ActivityResponse;
import com.manager.class_activity.qnu.dto.response.ClassActivityResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.entity.*;
import com.manager.class_activity.qnu.entity.Class;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.mapper.ClassActivityMapper;
import com.manager.class_activity.qnu.repository.ClassActivityRepository;
import com.manager.class_activity.qnu.repository.StudentPositionRepository;
import com.manager.class_activity.qnu.until.DateTimeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClassActivityService {

    ClassService classService;
    ActivityGuideService activityGuideService;
    StudentPositionRepository studentPositionRepository;
    ClassActivityMapper classActivityMapper;
    ActivityViewService activityViewService;
    ClassActivityRepository classActivityRepository;

    @Transactional(rollbackFor = Exception.class)
    public void createAllClassActivity(Activity activity) {
        for (Class clazz : classService.getClasses()) {
            StudentPosition position = studentPositionRepository
                    .findStudentPositionByClassIdAndPosition(clazz.getId(), StudentPositionEnum.ClassLeader);

            Student lead = (position != null) ? position.getStudent() : null;

            // Tạo ClassActivity
            ClassActivity classActivity = ClassActivity.builder()
                    .activity(activity)
                    .leader(lead)
                    .clazz(clazz)
                    .activityTime(Timestamp.valueOf(DateTimeUtils.getLastFridayOfCurrentMonthAt10AM()))
                    .build();

            // Lưu ClassActivity vào cơ sở dữ liệu
            classActivityRepository.save(classActivity);

            activityViewService.createActivityViewsForStudents(classActivity);
        }
    }

    public PagedResponse<ClassActivityResponse> getClassActivities(CustomPageRequest<?> request) {
        Page<ClassActivity> classActivityPage = classActivityRepository.getClassActivitiesByPaged(
                request.toPageable(),
                request.getDepartmentId(),
                request.getCourseId(),
                request.getClassId(),
                request.getActivityId()
        );
        System.out.println(request.getDepartmentId());
        System.out.println(request.getDepartmentId());
        System.out.println(request.getClassId());
        System.out.println(request.getCourseId());
        System.out.println(request.getKeyWord());
        System.out.println(request.getActivityId());



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



    public void updateActivityTime(int id, Timestamp newActivityTime) {
        ClassActivity classActivity = classActivityRepository.findById(id)
                .orElseThrow(() -> new BadException(ErrorCode.CLASS_ACTIVITY_NOT_FOUND));

        classActivity.setActivityTime(newActivityTime);

        classActivityRepository.save(classActivity);
    }

    public ClassActivityResponse getClassActivityResponse(int id) {
        ClassActivity classActivity = classActivityRepository.getClassActivityByIdAndIsDeleted(id, false).orElseThrow(
                ()-> new BadException(ErrorCode.CLASS_ACTIVITY_NOT_FOUND)
        );
        return classActivityMapper.toClassActivityResponse(classActivity);
    }
}
