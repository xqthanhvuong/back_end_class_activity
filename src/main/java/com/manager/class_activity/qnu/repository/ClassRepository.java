package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.Class;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<Class, Integer> {

    @Query("SELECT co from Class co where co.isDeleted = false " +
            "and (:keyword is NULL or lower(co.name) like lower(concat('%', :keyword, '%')) " +
            "or str(co.id) like lower(concat('%', :keyword, '%')))" +
            "and (:departmentId is null  or co.department.id = :departmentId)" +
            "and (:courseId is null or co.course.id = :courseId)")
    Page<Class> getClassesByPaged(Pageable pageable, String keyword, Integer departmentId, Integer courseId);

    Optional<Class> findByIdAndIsDeleted(int classId, boolean b);

    Class findByNameAndIsDeleted(String name, boolean b);

    List<Class> getAllByIsDeleted(boolean b);

    @Query("SELECT c FROM Class c WHERE (c.course.startYear + c.durationYears) >= :currentYear AND c.isDeleted = false")
    List<Class> findByStartYearAndDurationYearsGreaterThan(float currentYear);
}
