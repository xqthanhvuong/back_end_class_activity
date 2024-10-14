package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.DepartmentActivityGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentActivityGuideRepository extends JpaRepository<DepartmentActivityGuide, Integer> {
}
