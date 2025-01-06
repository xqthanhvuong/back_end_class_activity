package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.dto.response.ClassActivityResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.entity.Class;
import com.manager.class_activity.qnu.entity.*;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.mapper.ClassActivityMapper;
import com.manager.class_activity.qnu.repository.ClassActivityRepository;
import com.manager.class_activity.qnu.repository.MinutesOfClassActivitiesRepository;
import com.manager.class_activity.qnu.repository.StudentPositionRepository;
import com.manager.class_activity.qnu.until.DateTimeUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
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
    NotificationService notificationService;
    MinutesOfClassActivitiesRepository minutesOfClassActivitiesRepository;

    @Transactional(rollbackFor = Exception.class)
    public void createAllClassActivity(Activity activity) {
        for (Class clazz : classService.getClasses()) {
            StudentPosition position = studentPositionRepository
                    .findStudentPositionByClassIdAndPosition(clazz.getId(), StudentPositionEnum.ClassLeader);

            Student lead = (position != null) ? position.getStudent() : null;

            ClassActivity classActivity = ClassActivity.builder()
                    .activity(activity)
                    .leader(lead)
                    .clazz(clazz)
                    .activityTime(Timestamp.valueOf(DateTimeUtils.getLastFridayOfCurrentMonthAt10AM()))
                    .build();

            classActivityRepository.save(classActivity);
            notificationService.sendNotification(classActivity, "New class activity has been created");

            activityViewService.createActivityViewsForStudents(classActivity);
        }
    }

    public PagedResponse<ClassActivityResponse> getClassActivities(CustomPageRequest<?> request) {
        Page<ClassActivity> classActivityPage = classActivityRepository.getClassActivitiesByPaged(
                request.toPageable(),
                request.getDepartmentId(),
                request.getCourseId(),
                request.getClassId(),
                request.getActivityId(),
                request.getActivityStatus()
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



    public void updateActivityTime(int id, Timestamp newActivityTime) {
        ClassActivity classActivity = classActivityRepository.findById(id)
                .orElseThrow(() -> new BadException(ErrorCode.CLASS_ACTIVITY_NOT_FOUND));

        classActivity.setActivityTime(newActivityTime);
        classActivityRepository.save(classActivity);
        String message = String.format("Your class activity will start at %s", newActivityTime.toString());
        notificationService.sendNotification(classActivity,  message);
    }

    public ClassActivityResponse getClassActivityResponse(int id) {
        ClassActivity classActivity = classActivityRepository.getClassActivityByIdAndIsDeleted(id, false).orElseThrow(
                ()-> new BadException(ErrorCode.CLASS_ACTIVITY_NOT_FOUND)
        );
        return classActivityMapper.toClassActivityResponse(classActivity);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void setStatusForClassActivity() {
        classActivityRepository.updateStatusForClassActivities(Status.CANCELLED.name(), Status.COMPLETED.name());
    }


}
