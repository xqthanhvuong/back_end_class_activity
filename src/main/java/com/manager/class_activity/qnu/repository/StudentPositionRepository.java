package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.StudentPosition;
import com.manager.class_activity.qnu.entity.StudentPositionEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentPositionRepository extends JpaRepository<StudentPosition, Integer> {
    Optional<StudentPosition> findByIdAndIsDeleted(Integer id, Boolean isDeleted);

    @Query("SELECT sp FROM StudentPosition sp WHERE sp.student.id = :studentId AND sp.position = :position " +
            "AND sp.isDeleted = false ORDER BY sp.startDate DESC")
    Optional<StudentPosition> findLatestMemberPositionByStudentId(@Param("studentId") int studentId,
                                                                  @Param("position") StudentPositionEnum position);

    @Query("SELECT sp FROM StudentPosition sp WHERE sp.student.id = :studentId AND sp.endDate IS NULL " +
            "AND sp.isDeleted = false And sp.clazz.isDeleted = false " +
            "and sp.student.isDeleted = false ORDER BY sp.updatedAt DESC")
    StudentPosition findLatestActivePositionByStudentId(@Param("studentId") int studentId);

    @Query("SELECT sp FROM StudentPosition sp WHERE sp.clazz.id = :classId AND sp.endDate IS NULL " +
            "AND sp.isDeleted = false " +
            "And sp.clazz.isDeleted = false " +
            "and sp.student.isDeleted = false " +
            "ORDER BY sp.updatedAt DESC")
    List<StudentPosition> findCurrentPositionByClassId(Integer classId);

    @Query("SELECT sp FROM StudentPosition sp WHERE sp.clazz.id = :classId AND sp.endDate IS NULL " +
            "AND sp.isDeleted = false " +
            "And sp.clazz.isDeleted = false " +
            "and sp.student.isDeleted = false " +
            "and sp.position = :position")
    StudentPosition findStudentPositionByClassIdAndPosition(Integer classId, StudentPositionEnum position);

}
