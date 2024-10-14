package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.StudentPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentPositionRepository extends JpaRepository<StudentPosition, Integer> {
}
