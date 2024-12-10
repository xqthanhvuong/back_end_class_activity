package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.ActivityGuide;
import com.manager.class_activity.qnu.entity.DepartmentActivityGuide;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentActivityGuideRepository extends JpaRepository<DepartmentActivityGuide, Integer> {

    @Query("select de from DepartmentActivityGuide de where de.isDeleted= false " +
            "and de.activity.id = :id and " +
            "(de.department.id = :departmentId) ")
    List<DepartmentActivityGuide> findByActivityIdAndDepartmentId(int id, Integer departmentId);

    Optional<DepartmentActivityGuide> findByIdAndIsDeleted(int id, boolean b);

    @Query("Select de from DepartmentActivityGuide de where de.isDeleted = false " +
            "and (lower(de.name) like concat('%',lower(:keyWord),'%') or :keyWord is null ) and " +
            "de.department.id = :departmentId")
    Page<ActivityGuide> getBypage(Pageable pageable, String keyWord, Integer departmentId);
}
