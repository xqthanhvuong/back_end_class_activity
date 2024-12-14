ALTER TABLE `attendance_record`
    MODIFY `status` ENUM('Present', 'Absent', 'Excused', 'Late') DEFAULT 'Absent';
