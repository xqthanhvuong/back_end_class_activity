package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.dto.request.StudentPositionRequest;
import com.manager.class_activity.qnu.entity.*;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.mapper.StudentPositionMapper;
import com.manager.class_activity.qnu.repository.AccountRoleRepository;
import com.manager.class_activity.qnu.repository.StudentPositionRepository;
import com.manager.class_activity.qnu.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentPositionService {

    StudentPositionRepository studentPositionRepository;
    StudentRepository studentRepository;
    ClassService classService;
    StudentPositionMapper studentPositionMapper;
    AccountRoleRepository accountRoleRepository;
    RoleService roleService;

    @Transactional
    public void saveStudentPosition(StudentPositionRequest studentPositionRequest) {
        StudentPosition currentStudentPosition = studentPositionRepository.findLatestActivePositionByStudentId(studentPositionRequest.getStudentId());
        Student student = studentRepository.findByIdAndIsDeleted(studentPositionRequest.getStudentId(), false)
                .orElseThrow(() -> new BadException(ErrorCode.STUDENT_NOT_FOUND));

        if(ObjectUtils.isNotEmpty(currentStudentPosition)) {
            if (currentStudentPosition.getPosition().equals(studentPositionRequest.getPosition())) {
                throw new BadException(ErrorCode.DUPLICATE_POSITION);
            }
            currentStudentPosition.setEndDate(Date.valueOf(LocalDate.now()));
            studentPositionRepository.save(currentStudentPosition);

            if(!studentPositionRequest.getPosition().equals(StudentPositionEnum.Member)) {
                List<StudentPosition> studentPositions = studentPositionRepository.findCurrentPositionByClassId(studentPositionRequest.getClassId());
                for (StudentPosition item : studentPositions) {
                    if(item.getPosition().equals(studentPositionRequest.getPosition())) {
                        item.setEndDate(Date.valueOf(LocalDate.now()));
                        studentPositionRepository.save(item);
                        StudentPosition studentPosition = StudentPosition.builder()
                                .position(StudentPositionEnum.Member)
                                .clazz(item.getClazz())
                                .student(item.getStudent())
                                .build();
                        studentPositionRepository.save(studentPosition);
                        if(item.getPosition().equals(StudentPositionEnum.ClassLeader) && studentPositionRequest.getPosition().equals(StudentPositionEnum.ClassLeader)){
                            Account account = item.getStudent().getAccount();
                            AccountRole accountRole = accountRoleRepository.findByAccountAndRole(account,roleService.getRoleStudentLeader());
                            if(ObjectUtils.isNotEmpty(accountRole)){
                                accountRoleRepository.delete(accountRole);
                            }
                        }
                    }
                }
            }
        }
        if(studentPositionRequest.getPosition().equals(StudentPositionEnum.ClassLeader)) {
            Account account = student.getAccount();
            AccountRole accountRole = AccountRole.builder().account(account).role(roleService.getRoleStudentLeader()).build();
            accountRoleRepository.save(accountRole);
        }
        StudentPosition studentPosition = studentPositionMapper.toStudentPosition(studentPositionRequest);
        studentPosition.setClazz(classService.getClassById(studentPositionRequest.getClassId()));
        studentPosition.setStudent(student);
        studentPositionRepository.save(studentPosition);

    }


    public StudentPosition findStudentPositionById(int studentPositionId) {
        return studentPositionRepository.findByIdAndIsDeleted(studentPositionId,false)
                .orElseThrow(() -> new BadException(ErrorCode.STUDENT_POSITION_NOT_FOND));
    }

}
