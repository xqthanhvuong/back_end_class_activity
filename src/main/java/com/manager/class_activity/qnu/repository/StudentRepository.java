package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    @Modifying
    @Transactional
    @Query(value = """
    UPDATE student s
    JOIN class c ON c.id = s.class_id
    SET s.is_deleted = 1
    WHERE c.course_id = :courseId
""", nativeQuery = true)
    void deleteByCourseId(@Param("courseId") int courseId);


    @Modifying
    @Transactional
    @Query(value = """
    UPDATE student s
    JOIN class c ON c.id = s.class_id
    JOIN department d ON d.id = c.department_id
    SET s.is_deleted = 1
    WHERE d.id = :departmentId
""", nativeQuery = true)
    void deleteByDepartmentId(@Param("departmentId") int departmentId);

    @Modifying
    @Transactional
    @Query(value = """
    UPDATE student s
    SET s.is_deleted = 1
    WHERE s.class_id = :classId
""", nativeQuery = true)
    void deleteByClassId(@Param("classId") int classId);



}
