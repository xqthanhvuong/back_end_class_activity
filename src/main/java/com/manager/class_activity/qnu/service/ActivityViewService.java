package com.manager.class_activity.qnu.service;

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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
            ActivityView activityView = activityViewRepository.getByStudentIdAndActivityId(student.getId(), activityId);
            activityView.setViewTime(Timestamp.valueOf(LocalDateTime.now()));
            activityViewRepository.save(activityView);
        }
    }

}
