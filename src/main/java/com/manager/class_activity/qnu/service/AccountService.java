package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.entity.*;
import com.manager.class_activity.qnu.entity.Class;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.repository.AcademicAdvisorRepository;
import com.manager.class_activity.qnu.repository.AccountRepository;
import com.manager.class_activity.qnu.until.AcademicYearUtil;
import com.manager.class_activity.qnu.until.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountService {
    AccountRepository accountRepository;
    AcademicAdvisorRepository academicAdvisorRepository;

    public void saveAccount(Account account) {
        if(accountRepository.existsByUsernameAndIsDeleted(account.getUsername(), false)) {
            throw new BadException(ErrorCode.USER_EXISTED);
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        accountRepository.save(account);
    }

    public void deleteAccount(int id) {
        Account account = getAccount(id);
        account.setDeleted(true);
        accountRepository.save(account);
    }
    public Account getAccount(int id) {
        return accountRepository.findByIdAndIsDeleted(id,false)
                .orElseThrow(()->new BadException(ErrorCode.USER_NOT_EXISTED));
    }
    public Account getAccount(String username) {
        return accountRepository.findByUsernameAndIsDeleted(username,false).orElseThrow(
                ()->new BadException(ErrorCode.USER_NOT_EXISTED)
        );
    }

    public Class getClassOfAccount(){
        String username = SecurityUtils.getCurrentUsername();
        Student student = accountRepository.getStudentByUsername(username);
        if(ObjectUtils.isNotEmpty(student)){
            return student.getClazz();
        }
        return null;
    }

    public List<Class> getMyClassOfAccountLecturer(){
        String username = SecurityUtils.getCurrentUsername();
        Lecturer lecturer = accountRepository.getLecturerByUsername(username);
        if(ObjectUtils.isNotEmpty(lecturer)){
            List<Class> classes = new ArrayList<>();
            List<AcademicAdvisor> advisors = academicAdvisorRepository.findByAcademicYearAndLecturerAndIsDeletedOrderByUpdatedAt(AcademicYearUtil.getCurrentAcademicYear(),lecturer,false);
            for (AcademicAdvisor item: advisors) {
                classes.add(item.getClazz());
            }
            return classes;
        }
        return null;
    }

    public Department getDepartmentOfAccount() {
        String username = SecurityUtils.getCurrentUsername();
        Lecturer lecturer = accountRepository.getLecturerByUsername(username);
        Staff staff;
        Student student;
        Department department = null;
        if(lecturer == null) {
            staff = accountRepository.getStaffByUsername(username);
            if(staff == null) {
                student = accountRepository.getStudentByUsername(username);
                if(student == null) {
                    return null;
                }
                department = student.getClazz().getDepartment();
                return department;
            }
            department = staff.getDepartment();
            return department;
        }
        department = lecturer.getDepartment();
        return department;
    }


    public String getNameOfAccount(String username) {
        Account account = accountRepository.findByUsernameAndIsDeleted(username,false).orElseThrow(
                ()->new BadException(ErrorCode.USER_NOT_EXISTED)
        );
        if(ObjectUtils.isNotEmpty(accountRepository.getLecturerByUsername(account.getUsername()))){
            return accountRepository.getLecturerByUsername(account.getUsername()).getName();
        }
        if(ObjectUtils.isNotEmpty(accountRepository.getStaffByUsername(account.getUsername()))){
            return accountRepository.getStaffByUsername(account.getUsername()).getName();
        }
        if(ObjectUtils.isNotEmpty(accountRepository.getStudentByUsername(account.getUsername()))){
            return accountRepository.getStudentByUsername(account.getUsername()).getName();
        }
        return "Admin";
    }


}
