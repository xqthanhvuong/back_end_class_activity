package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.dto.request.MinutesActivityRequest;
import com.manager.class_activity.qnu.dto.response.MinutesActivityResponse;
import com.manager.class_activity.qnu.entity.ClassActivity;
import com.manager.class_activity.qnu.entity.MinutesOfClassActivities;
import com.manager.class_activity.qnu.entity.Status;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.repository.ClassActivityRepository;
import com.manager.class_activity.qnu.repository.MinutesOfClassActivitiesRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MinutesActivitiesService {
    MinutesOfClassActivitiesRepository minutesOfClassActivitiesRepository;
    ClassActivityRepository classActivityRepository;
    AttendanceSessionService attendanceSessionService;

    public void save(MinutesActivityRequest activityRequest) {
        ClassActivity classActivity = classActivityRepository
                .getClassActivityByIdAndIsDeleted(activityRequest.getClassActivityId(),false)
                .orElseThrow(
                () -> new RuntimeException("ClassActivity not found")
        );
        if(!attendanceSessionService.isInClass(classActivity.getClazz())){
            throw new BadException(ErrorCode.ACCESS_DENIED);
        }
        if(classActivity.getStatus().equals(Status.PLANNED) || classActivity.getStatus().equals(Status.CANCELLED)){
            throw new BadException(ErrorCode.ACTIVITY_NOT_YET_BEGIN);
        }
        MinutesOfClassActivities minutesOfClassActivities = minutesOfClassActivitiesRepository.findByClassActivity_Id(classActivity.getId());
        if(ObjectUtils.isNotEmpty(minutesOfClassActivities)){
            minutesOfClassActivities.setThisMonthActivity(activityRequest.getThisMonthActivity());
            minutesOfClassActivities.setClassFeedback(activityRequest.getClassFeedback());
            minutesOfClassActivities.setLastMonthActivity(activityRequest.getLastMonthActivity());
            minutesOfClassActivities.setTeacherFeedback(activityRequest.getTeacherFeedback());
        }else {
            minutesOfClassActivities = MinutesOfClassActivities.builder()
                    .classFeedback(activityRequest.getClassFeedback())
                    .thisMonthActivity(activityRequest.getThisMonthActivity())
                    .lastMonthActivity(activityRequest.getLastMonthActivity())
                    .classActivity(classActivity)
                    .teacherFeedback(activityRequest.getTeacherFeedback())
                    .build();
        }
        minutesOfClassActivitiesRepository.save(minutesOfClassActivities);
        classActivity.setStatus(Status.COMPLETED);
        classActivityRepository.save(classActivity);
    }

    public MinutesActivityResponse getMinutesActivity(Integer classActivityId){
        ClassActivity classActivity = classActivityRepository
                .getClassActivityByIdAndIsDeleted(classActivityId,false)
                .orElseThrow(
                        () -> new RuntimeException("ClassActivity not found")
                );
        MinutesOfClassActivities minutesOfClassActivities = minutesOfClassActivitiesRepository.findByClassActivity_Id(classActivity.getId());
        if(ObjectUtils.isNotEmpty(minutesOfClassActivities)){
            return MinutesActivityResponse.builder()
                    .id(minutesOfClassActivities.getId())
                    .classActivityId(minutesOfClassActivities.getClassActivity().getId())
                    .classFeedback(minutesOfClassActivities.getClassFeedback())
                    .lastMonthActivity(minutesOfClassActivities.getLastMonthActivity())
                    .teacherFeedback(minutesOfClassActivities.getTeacherFeedback())
                    .thisMonthActivity(minutesOfClassActivities.getThisMonthActivity())
                    .build();
        }else {
            return MinutesActivityResponse.builder()
                    .classActivityId(classActivityId)
                    .build();
        }
    }
}
