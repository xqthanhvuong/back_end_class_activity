package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.AcademicAdvisor;
import com.manager.class_activity.qnu.entity.Class;
import com.manager.class_activity.qnu.entity.Lecturer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicAdvisorRepository extends JpaRepository<AcademicAdvisor, Integer> {
    List<AcademicAdvisor> findAllByIsDeletedFalse();
    Optional<AcademicAdvisor> findByIdAndIsDeletedFalse(int id);
    @Query("SELECT st from AcademicAdvisor st where st.isDeleted = false " +
            "and (:keyword is NULL or lower(st.lecturer.name) like lower(concat('%', :keyword, '%')) " +
            "or str(st.id) like lower(concat('%', :keyword, '%')) " +
            "or str(st.lecturer.id) like lower(concat('%', :keyword, '%')))" +
            "and (:departmentId is Null or st.clazz.department.id = :departmentId)" +
            "and (:courseId is null or st.clazz.course.id = :courseId)" +
            "and (:classId is null or st.clazz.id = :classId)")
    Page<AcademicAdvisor> getAdvisorsByPaged(Pageable pageable, String keyword, Integer departmentId, Integer courseId, Integer classId);

    @Query("SELECT st from AcademicAdvisor st where st.isDeleted = false " +
            "and st.clazz.id = :classId " +
            "and st.academicYear like :academicYear")
    AcademicAdvisor getAdvisorByClassIdAndAcademicYear(Integer classId, String academicYear);

    AcademicAdvisor findTopByClazzIdAndIsDeletedOrderByCreatedAtDesc(int classId, boolean isDeleted);

    List<AcademicAdvisor> findByAcademicYearAndLecturerAndIsDeletedOrderByUpdatedAt(String academicYear, Lecturer lecturer, boolean isDeleted);

    List<AcademicAdvisor> findByAcademicYearAndClazzAndIsDeletedOrderByUpdatedAt(String academicYear, Class clazz, boolean isDeleted);



//    @Query("SELECT DISTINCT st.lecturer FROM AcademicAdvisor st " +
//            "WHERE st.isDeleted = false " +
//            "AND (:keyword IS NULL OR LOWER(st.lecturer.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
//            "OR STR(st.lecturer.id) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
//            "AND (:departmentId IS NULL OR st.clazz.department.id = :departmentId) " +
//            "AND (:courseId IS NULL OR st.clazz.course.id = :courseId) " +
//            "AND (:classId IS NULL OR st.clazz.id = :classId)")
//    Page<Lecturer> getUniqueLecturersByPaged(Pageable pageable, String keyword, Integer departmentId, Integer courseId, Integer classId);



}
