package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Integer> {
    AttendanceRecord findByAttendanceSession_IdAndStudent_Id(Integer attendanceSession_Id, Integer student_Id);
    List<AttendanceRecord> findByAttendanceSession_Id(Integer attendanceSession_Id);
}
