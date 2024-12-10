package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.ActivityGuide;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityGuideRepository extends JpaRepository<ActivityGuide, Integer> {
    ActivityGuide findByNameAndIsDeleted(String name,boolean isDeleted);

    @Query("select a from ActivityGuide a where a.isDeleted = false and " +
            "(a.name like lower(concat('%', :keyword, '%')) or :keyword is null )")
    Page<ActivityGuide> getBypage(Pageable pageable, String keyword);

    List<ActivityGuide> findByActivity_IdAndIsDeleted(Integer activityId,boolean isDeleted);

    List<ActivityGuide> findAllByIsDeleted(boolean b);

    Optional<ActivityGuide> findByIdAndIsDeleted(Integer id, boolean isDeleted);
}
