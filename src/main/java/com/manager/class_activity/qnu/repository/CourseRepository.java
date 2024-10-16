package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    Optional<Course> findByIdAndIsDeleted(int id, boolean isDeleted);

    @Query("SELECT co from Course co where co.isDeleted = false " +
            "and (:keyword is NULL or lower(co.name) like lower(concat('%', :keyword, '%')) " +
            "or str(co.id) like lower(concat('%', :keyword, '%')))")
    Page<Course> getCoursesByPaged(Pageable pageable, String keyword);
}
