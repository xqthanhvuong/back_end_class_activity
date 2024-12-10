package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeRepository extends JpaRepository<Type, Integer> {
    Optional<Type> findByName(String name);
}
