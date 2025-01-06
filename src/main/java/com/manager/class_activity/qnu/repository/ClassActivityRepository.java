package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.ClassActivity;
import com.manager.class_activity.qnu.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassActivityRepository extends JpaRepository<ClassActivity, Integer> {
    @Query("SELECT co from ClassActivity co where co.isDeleted = false " +
            "and (:departmentId is null or co.clazz.department.id = :departmentId) " +
            "and (:courseId is null or co.clazz.course.id = :courseId) " +
            "and (:classId is NULL or co.clazz.id = :classId) " +
            "and (:activityId is null or co.activity.id = :activityId) " +
            "and (:activityStatusList is null or co.status IN (:activityStatusList))")
    Page<ClassActivity> getClassActivitiesByPaged(Pageable pageable,
                                                  Integer departmentId,
                                                  Integer courseId,
                                                  Integer classId,
                                                  Integer activityId,
                                                  List<Status> activityStatusList
    );

    Optional<ClassActivity> getClassActivityByIdAndIsDeleted(Integer id, Boolean isDeleted);


    @Modifying
    @Transactional
    @Query(value = "UPDATE class_activity " +
            "SET status = :newStatus " +
            "WHERE DATE(activity_time) = DATE(NOW() - INTERVAL 1 DAY) " +
            "AND is_deleted = false " +
            "AND status != :excludedStatus",
            nativeQuery = true)
    void updateStatusForClassActivities(@Param("newStatus") String newStatus,
                                        @Param("excludedStatus") String excludedStatus);

}
