package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.dto.request.FilterClassActivity;
import com.manager.class_activity.qnu.dto.response.ActivityViewResponse;
import com.manager.class_activity.qnu.entity.ActivityView;
import com.manager.class_activity.qnu.entity.ClassActivity;
import com.manager.class_activity.qnu.entity.Student;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.repository.ActivityViewRepository;
import com.manager.class_activity.qnu.repository.StudentRepository;
import com.manager.class_activity.qnu.until.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActivityViewService {
    ActivityViewRepository activityViewRepository;
    AccountService accountService;
    StudentRepository studentRepository;

    @Async
    public void createActivityViewsForStudents(ClassActivity classActivity) {
        List<Student> students = studentRepository.findByClazz_IdAndIsDeleted(classActivity.getClazz().getId(),false);
        for (Student student : students) {
            ActivityView activityView = ActivityView.builder()
                    .student(student)
                    .classActivity(classActivity)
                    .build();
            activityViewRepository.save(activityView);
        }
    }

    public void watched(int activityId){
        if(Objects.equals(SecurityUtils.getCurrentUserType(), "STUDENT")){
            Student student = accountService.getAccount(SecurityUtils.getCurrentUsername()).getStudents().stream().findFirst().orElseThrow(
                    ()->new BadException(ErrorCode.USER_NOT_EXISTED)
            );
            System.out.println(student.getId());
            ActivityView activityView = activityViewRepository.getByStudentIdAndActivityId(student.getId(), activityId);
            System.out.println(activityView.getClassActivity().getId());
            if(ObjectUtils.isNotEmpty(activityView.getViewTime())){
                return;
            }
            activityView.setViewTime(Timestamp.valueOf(LocalDateTime.now()));
            activityView.setReaded(true);
            activityViewRepository.save(activityView);
        }
    }


    public List<ActivityViewResponse> getActivityView(FilterClassActivity filterClassActivity){
        List<ActivityView> activityViews = activityViewRepository.findByFilter(
                filterClassActivity.getActivityId(),
                filterClassActivity.getDepartmentId(),
                filterClassActivity.getClassId(),
                filterClassActivity.getCourseId()
        );
        List<ActivityViewResponse> activityViewResponses = new ArrayList<>();
        for (ActivityView item: activityViews) {
            activityViewResponses.add(ActivityViewResponse.builder()
                            .studentCode(item.getStudent().getStudentCode())
                            .viewTime(item.getViewTime())
                            .isReaded(item.isReaded())
                            .studentName(item.getStudent().getName())
                    .build());
        }
        return activityViewResponses;
    }



}
