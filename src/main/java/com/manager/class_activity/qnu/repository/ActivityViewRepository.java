package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.ActivityView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityViewRepository extends JpaRepository<ActivityView, Integer> {

    @Query("select st from ActivityView st where st.student.id = :studentId and " +
            "st.classActivity.id = :activityId")
    ActivityView getByStudentIdAndActivityId(int studentId, int activityId);

    @Query("select a from ActivityView a where " +
            "(a.classActivity.clazz.id = :classId or :classId is null) and  " +
            "(a.classActivity.activity.id = :activityId or :activityId is null) and " +
            "(a.classActivity.clazz.department.id = :departmentId or :departmentId is null) and " +
            "(a.classActivity.clazz.course.id = :courseId or :courseId is null)")
    List<ActivityView> findByFilter(Integer activityId, Integer departmentId, Integer classId, Integer courseId);

    @Query("SELECT COUNT(av) FROM ActivityView av WHERE av.classActivity.id = :classActivityId AND av.isReaded = true")
    long countReadByClassActivityId(@Param("classActivityId") int classActivityId);

    @Query("SELECT COUNT(av) FROM ActivityView av WHERE av.classActivity.id = :classActivityId")
    long countAllByClassActivityId(@Param("classActivityId") int classActivityId);
}
