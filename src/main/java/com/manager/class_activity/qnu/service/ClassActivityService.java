package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.repository.ClassActivityRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClassActivityService {
    ClassActivityRepository classActivityRepository;

    public void createAllClassActivity(){
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();

    }

}
