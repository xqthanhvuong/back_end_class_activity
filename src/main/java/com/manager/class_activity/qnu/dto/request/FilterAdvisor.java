package com.manager.class_activity.qnu.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilterAdvisor extends FilterClass{
    Integer classId;
    public FilterAdvisor(String name, Integer departmentId, Integer courseId) {
        super(name, departmentId, courseId);
    }
}
