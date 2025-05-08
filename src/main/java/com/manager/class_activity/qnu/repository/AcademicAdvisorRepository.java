package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.entity.AcademicAdvisor;
import com.manager.class_activity.qnu.entity.Class;
import com.manager.class_activity.qnu.entity.Lecturer;
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
public interface AcademicAdvisorRepository extends JpaRepository<AcademicAdvisor, Integer> {
    List<AcademicAdvisor> findAllByIsDeletedFalse();
    Optional<AcademicAdvisor> findByIdAndIsDeletedFalse(int id);
    @Query("SELECT st from AcademicAdvisor st where st.isDeleted = false " +
            "and (:keyword is NULL or lower(st.lecturer.name) like lower(concat('%', :keyword, '%')) " +
            "or str(st.id) like lower(concat('%', :keyword, '%')) " +
            "or str(st.lecturer.id) like lower(concat('%', :keyword, '%')))" +
            "and (:departmentId is Null or st.clazz.department.id = :departmentId)" +
            "and (:courseId is null or st.clazz.course.id = :courseId)" +
            "and (:classId is null or st.clazz.id = :classId) " +
            "and (:academicYear is null or st.academicYear = :academicYear)")
    Page<AcademicAdvisor> getAdvisorsByPaged(Pageable pageable, String keyword, Integer departmentId, Integer courseId, Integer classId, String academicYear);

    @Query("SELECT st from AcademicAdvisor st where st.isDeleted = false " +
            "and st.clazz.id = :classId " +
            "and st.academicYear like :academicYear")
    AcademicAdvisor getAdvisorByClassIdAndAcademicYear(Integer classId, String academicYear);

    AcademicAdvisor findTopByClazzIdAndIsDeletedOrderByCreatedAtDesc(int classId, boolean isDeleted);

    List<AcademicAdvisor> findByAcademicYearAndLecturerAndIsDeletedOrderByUpdatedAt(String academicYear, Lecturer lecturer, boolean isDeleted);

    List<AcademicAdvisor> findByAcademicYearAndClazzAndIsDeletedOrderByUpdatedAt(String academicYear, Class clazz, boolean isDeleted);

    Boolean existsAcademicAdvisorByAcademicYearAndClazzAndLecturer(String year, Class clazz, Lecturer lecturer);


    @Query("Select aa.academicYear from AcademicAdvisor aa group by aa.academicYear")
    List<String> getAcademicYears();

    @Modifying
    @Transactional
    @Query("update AcademicAdvisor s set s.isDeleted = true where s.clazz.department.id = :departmentId")
    void deleteByDepartmentId(int departmentId);


    @Modifying
    @Transactional
    @Query(value = """
    UPDATE academic_advisor aa
    JOIN class c ON c.id = aa.class_id
    SET aa.is_deleted = 1
    WHERE c.course_id = :courseId
""", nativeQuery = true)
    void deleteByCourseId(@Param("courseId") int courseId);



    @Modifying
    @Transactional
    @Query("update AcademicAdvisor a set a.isDeleted = true where a.clazz.id = :classId")
    void deleteByClassId(int classId);



//    @Query("SELECT DISTINCT st.lecturer FROM AcademicAdvisor st " +
//            "WHERE st.isDeleted = false " +
//            "AND (:keyword IS NULL OR LOWER(st.lecturer.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
//            "OR STR(st.lecturer.id) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
//            "AND (:departmentId IS NULL OR st.clazz.department.id = :departmentId) " +
//            "AND (:courseId IS NULL OR st.clazz.course.id = :courseId) " +
//            "AND (:classId IS NULL OR st.clazz.id = :classId)")
//    Page<Lecturer> getUniqueLecturersByPaged(Pageable pageable, String keyword, Integer departmentId, Integer courseId, Integer classId);



}
