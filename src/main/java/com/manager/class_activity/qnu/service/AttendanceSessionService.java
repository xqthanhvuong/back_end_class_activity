package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.entity.*;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.repository.*;
import com.manager.class_activity.qnu.until.RandomNumberGenerator;
import com.manager.class_activity.qnu.until.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttendanceSessionService {
    AttendanceSessionRepository attendanceSessionRepository;
    ClassActivityRepository classActivityRepository;
    AccountRepository accountRepository;
    AcademicAdvisorService academicAdvisorService;
    StudentRepository studentRepository;

    public void createAttendanceSession(int activityId){
        ClassActivity classActivity = classActivityRepository.getClassActivityByIdAndIsDeleted(activityId,false).orElseThrow(
                ()->new BadException(ErrorCode.CLASS_ACTIVITY_NOT_FOUND)
        );
        if(!isInClass(classActivity)){
            throw new BadException(ErrorCode.ACCESS_DENIED);
        }
        AttendanceSession attendanceSession = AttendanceSession.builder()
                .attendanceCode(RandomNumberGenerator.generateRandomSixDigit())
                .classActivity(classActivity)
                .build();
        List<Student> students = studentRepository.findByClazz_IdAndIsDeleted(classActivity.getClazz().getId(),false);
        Set<AttendanceRecord> attendanceRecordSet = new HashSet<>();
        for (Student st: students) {
            attendanceRecordSet.add(AttendanceRecord.builder()
                            .attendanceSession(attendanceSession)
                            .student(st)
                    .build());
        }
        attendanceSession.setAttendanceRecords(attendanceRecordSet);
        attendanceSessionRepository.save(attendanceSession);
    }


    public boolean isInClass(ClassActivity classActivity){
        Student student = accountRepository.getStudentByUsername(SecurityUtils.getCurrentUsername());
        if(ObjectUtils.isNotEmpty(student)){
            return student.getClazz().equals(classActivity.getClazz());
        }
        Lecturer lecturer = accountRepository.getLecturerByUsername(SecurityUtils.getCurrentUsername());
        if(ObjectUtils.isNotEmpty(lecturer)){
            return lecturer.equals(academicAdvisorService.getAdvisorOfClass(classActivity.getId()));
        }
        return false;
    }
//
//    public AttendanceSession getAttendanceSession(Student student){
//        return attendanceSessionRepository.
//
//    }


}
