package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    @Query("SELECT st from Student st where st.isDeleted = false " +
            "and (:keyword is NULL or lower(st.name) like lower(concat('%', :keyword, '%')) " +
            "or str(st.id) like lower(concat('%', :keyword, '%')) " +
            "or st.studentCode like lower(concat('%', :keyword, '%')))" +
            "and (:departmentId is Null or st.clazz.department.id = :departmentId)" +
            "and (:courseId is null or st.clazz.course.id = :courseId)" +
            "and (:classId is null or st.clazz.id = :classId)")
    Page<Student> getStudentsByPaged(Pageable pageable, String keyword, Integer departmentId, Integer courseId, Integer classId);

    Optional<Student> findByIdAndIsDeleted(int studentId, boolean isDeleted);

    List<Student> findByClazz_IdAndIsDeleted(int clazzId, boolean isDeleted);

    Student findByStudentCodeAndIsDeleted(String studentCode, boolean isDeleted);

}
