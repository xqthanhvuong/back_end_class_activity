package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.ActivityView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityViewRepository extends JpaRepository<ActivityView, Integer> {

    @Query("select st from ActivityView st where st.student.id = :studentId and " +
            "st.classActivity.id = :activityId")
    ActivityView getByStudentIdAndActivityId(int studentId, int activityId);
}
