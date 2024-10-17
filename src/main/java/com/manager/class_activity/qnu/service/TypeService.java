package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.entity.Type;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.repository.TypeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TypeService {
    TypeRepository typeRepository;
    public Type getTypeDepartment(){
        return typeRepository.findById(2)
                .orElseThrow(()->new BadException(ErrorCode.UNCATEGORIZED_EXCEPTION));
    }
}
