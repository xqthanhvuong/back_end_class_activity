package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.ClassActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassActivityRepository extends JpaRepository<ClassActivity, Integer> {
    @Query("SELECT co from ClassActivity co where co.isDeleted = false " +
            "and (:departmentId is null or co.clazz.department.id = :departmentId) " +
            "and (:courseId is null or co.clazz.course.id = :courseId) " +
            "and (:classId is NULL or co.clazz.id = :classId) " +
            "and (:activityId is null or co.id = :activityId)")
    Page<ClassActivity> getClassActivitiesByPaged(Pageable pageable,
                                                  Integer departmentId,
                                                  Integer courseId,
                                                  Integer classId,
                                                  Integer activityId);

    Optional<ClassActivity> getClassActivityByIdAndIsDeleted(Integer id, Boolean isDeleted);
}
