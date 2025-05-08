package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.dto.request.AttendanceRecordRequest;
import com.manager.class_activity.qnu.dto.response.AttendanceRecordResponse;
import com.manager.class_activity.qnu.dto.response.AttendanceSessionResponse;
import com.manager.class_activity.qnu.dto.response.RollCallResponse;
import com.manager.class_activity.qnu.entity.*;
import com.manager.class_activity.qnu.entity.Class;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.repository.*;
import com.manager.class_activity.qnu.until.DateTimeUtils;
import com.manager.class_activity.qnu.until.RandomNumberGenerator;
import com.manager.class_activity.qnu.until.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log4j2
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttendanceSessionService {
    AttendanceSessionRepository attendanceSessionRepository;
    ClassActivityRepository classActivityRepository;
    AccountRepository accountRepository;
    AcademicAdvisorService academicAdvisorService;
    StudentRepository studentRepository;
    AttendanceRecordRepository attendanceRecordRepository;

    public AttendanceSessionResponse createAttendanceSession(int activityId){
        ClassActivity classActivity = classActivityRepository.getClassActivityByIdAndIsDeleted(activityId,false).orElseThrow(
                ()->new BadException(ErrorCode.CLASS_ACTIVITY_NOT_FOUND)
        );
        if(!isInClass(classActivity.getClazz())){
            throw new BadException(ErrorCode.ACCESS_DENIED);
        }
        if(!DateTimeUtils.compareTimestamp(classActivity.getActivityTime())){
            throw new BadException(ErrorCode.ACTIVITY_NOT_YET_BEGIN);
        }
        AttendanceSession attendanceSession = attendanceSessionRepository.findByClassActivity_Id(activityId);
        if(ObjectUtils.isNotEmpty(attendanceSession)){
            if(attendanceSession.getEndTime().before(Timestamp.valueOf(LocalDateTime.now()))){
                throw new BadException(ErrorCode.ATTENDANCE_SESSION_IS_END);
            }
            return AttendanceSessionResponse.builder()
                    .updateAt(attendanceSession.getUpdatedAt())
                    .attendanceCode(attendanceSession.getAttendanceCode())
                    .build();
        }
        attendanceSession = AttendanceSession.builder()
                .attendanceCode(RandomNumberGenerator.generateRandomSixDigit())
                .classActivity(classActivity)
                .build();
        List<Student> students = studentRepository.findByClazz_IdAndIsDeleted(classActivity.getClazz().getId(),false);
        Set<AttendanceRecord> attendanceRecordSet = new HashSet<>();
        for (Student st: students) {
            attendanceRecordSet.add(AttendanceRecord.builder()
                            .attendanceSession(attendanceSession)
                            .status(AttendanceStatusEnum.Absent)
                            .student(st)
                    .build());
        }
        attendanceSession.setAttendanceRecords(attendanceRecordSet);
        attendanceSessionRepository.save(attendanceSession);
        classActivity.setStatus(Status.ONGOING);
        classActivityRepository.save(classActivity);
        return AttendanceSessionResponse.builder()
                .updateAt(attendanceSession.getUpdatedAt())
                .attendanceCode(attendanceSession.getAttendanceCode())
                .build();
    }


    public AttendanceSessionResponse renewCode(int activityId){
        ClassActivity classActivity = classActivityRepository.getClassActivityByIdAndIsDeleted(activityId,false).orElseThrow(
                ()->new BadException(ErrorCode.CLASS_ACTIVITY_NOT_FOUND)
        );
        if(!isInClass(classActivity.getClazz())){
            throw new BadException(ErrorCode.ACCESS_DENIED);
        }
        if(!DateTimeUtils.compareTimestamp(classActivity.getActivityTime())){
            throw new BadException(ErrorCode.ACTIVITY_NOT_YET_BEGIN);
        }
        AttendanceSession attendanceSession = attendanceSessionRepository.findByClassActivity_Id(activityId);

        if(ObjectUtils.isEmpty(attendanceSession)){
            throw new BadException(ErrorCode.ATTENDANCE_SESSION_NOT_FOUND);
        }

        attendanceSession.setAttendanceCode(RandomNumberGenerator.generateRandomSixDigit());
        attendanceSession = attendanceSessionRepository.save(attendanceSession);

        return AttendanceSessionResponse.builder()
                .updateAt(attendanceSession.getUpdatedAt())
                .attendanceCode(attendanceSession.getAttendanceCode())
                .build();
    }


    public boolean isInClass(Class clazz){
        Student student = accountRepository.getStudentByUsername(SecurityUtils.getCurrentUsername());
        if(ObjectUtils.isNotEmpty(student)){
            return student.getClazz().equals(clazz);
        }
        Lecturer lecturer = accountRepository.getLecturerByUsername(SecurityUtils.getCurrentUsername());
        if(ObjectUtils.isNotEmpty(lecturer)){
            return lecturer.equals(academicAdvisorService.getAdvisorOfClass(clazz));
        }
        return false;
    }

    public RollCallResponse rollCall(int classActivityId, String attendanceCode){
        ClassActivity classActivity = classActivityRepository.getClassActivityByIdAndIsDeleted(classActivityId,false).orElseThrow(
                ()->new BadException(ErrorCode.CLASS_ACTIVITY_NOT_FOUND)
        );
        if(classActivity.getStatus().equals(Status.COMPLETED)){
            throw new BadException(ErrorCode.ACTIVITY_COMPLETED);
        }
        Student st = accountRepository.getStudentByUsername(SecurityUtils.getCurrentUsername());
        if(ObjectUtils.isEmpty(st)){
            throw new BadException(ErrorCode.ACCESS_DENIED);
        }
        if(!isInClass(classActivity.getClazz())){
            throw new BadException(ErrorCode.ACCESS_DENIED);
        }
        AttendanceSession attendanceSession = attendanceSessionRepository.findByClassActivity_Id(classActivityId);

        if(!attendanceCode.equals(attendanceSession.getAttendanceCode())){
            log.info("wrong code");
            return new RollCallResponse(false);
        }
        if(attendanceSession.getUpdatedAt().toLocalDateTime().plusSeconds(30).isBefore(LocalDateTime.now())){
            log.info("code expired");
            return new RollCallResponse(false);
        }
        AttendanceRecord attendanceRecord = attendanceRecordRepository.findByAttendanceSession_IdAndStudent_Id(attendanceSession.getId(),st.getId());
        attendanceRecord.setStatus(AttendanceStatusEnum.getStatusBasedOnTime(classActivity.getActivityTime()));
        attendanceRecord.setCheckInTime(Timestamp.valueOf(LocalDateTime.now()));
        attendanceRecordRepository.save(attendanceRecord);
        return new RollCallResponse(true);
    }

    public List<AttendanceRecordResponse> getAttendanceRecords(int classActivityId){
        ClassActivity classActivity = classActivityRepository.getClassActivityByIdAndIsDeleted(classActivityId,false).orElseThrow(
                ()->new BadException(ErrorCode.CLASS_ACTIVITY_NOT_FOUND)
        );
        AttendanceSession attendanceSession = attendanceSessionRepository.findByClassActivity_Id(classActivity.getId());
        List<AttendanceRecordResponse> attendanceRecordResponses = new ArrayList<>();
        if(ObjectUtils.isNotEmpty(attendanceSession)){
            List<AttendanceRecord> attendanceRecords = attendanceRecordRepository.findByAttendanceSession_Id(attendanceSession.getId());
            for(AttendanceRecord attendanceRecord: attendanceRecords){
                attendanceRecordResponses.add(AttendanceRecordResponse.builder()
                        .id(attendanceRecord.getId())
                        .checkInTime(attendanceRecord.getCheckInTime())
                        .status(attendanceRecord.getStatus())
                        .studentCode(attendanceRecord.getStudent().getStudentCode())
                        .studentName(attendanceRecord.getStudent().getName())
                        .createdAt(attendanceRecord.getCreatedAt())
                        .updatedAt(attendanceRecord.getUpdatedAt())
                        .build());
            }
        }
        return attendanceRecordResponses;
    }

    public void updateAttendanceRecord(AttendanceRecordRequest request){
        AttendanceRecord attendanceRecord = attendanceRecordRepository.findById(request.getId()).orElseThrow(
                ()-> new BadException(ErrorCode.NOT_FOND)
        );
        if(ObjectUtils.isEmpty(attendanceRecord.getCheckInTime())){
            if(request.getAttendanceStatus().equals(AttendanceStatusEnum.Late) || request.getAttendanceStatus().equals(AttendanceStatusEnum.Present)){
                attendanceRecord.setCheckInTime(Timestamp.valueOf(LocalDateTime.now()));
            }
        }else {
            if(request.getAttendanceStatus().equals(AttendanceStatusEnum.Excused) || request.getAttendanceStatus().equals(AttendanceStatusEnum.Absent)){
                attendanceRecord.setCheckInTime(null);
            }
        }
        
        attendanceRecord.setStatus(request.getAttendanceStatus());
        attendanceRecordRepository.save(attendanceRecord);
    }

}
