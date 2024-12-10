package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
    Activity findByNameAndIsDeleted(String name, boolean deleted);

    Optional<Activity> findByIdAndIsDeleted(int id, boolean deleted);

    @Query("select a from Activity a where a.isDeleted=false " +
            "and (:keyWord is null or a.name like lower(concat('%', :keyWord, '%')))")
    Page<Activity> findByPaged(Pageable pageable, String keyWord);

    List<Activity> findByIsDeleted(boolean deleted);
}
