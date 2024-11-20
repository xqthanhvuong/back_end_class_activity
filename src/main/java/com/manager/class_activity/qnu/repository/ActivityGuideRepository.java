package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.ActivityGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityGuideRepository extends JpaRepository<ActivityGuide, Integer> {
    ActivityGuide findByNameAndIsDeleted(String name,boolean isDeleted);
}
