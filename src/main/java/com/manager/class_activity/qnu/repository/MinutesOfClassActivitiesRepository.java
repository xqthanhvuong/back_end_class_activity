package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.MinutesOfClassActivities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MinutesOfClassActivitiesRepository extends JpaRepository<MinutesOfClassActivities, Integer> {
    MinutesOfClassActivities findByClassActivity_Id(Integer classActivityId);
}
