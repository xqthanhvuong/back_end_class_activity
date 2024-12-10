package com.manager.class_activity.qnu.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilterClassActivity extends FilterStudent{
    Integer activityId;
    public FilterClassActivity(String name, Integer departmentId, Integer courseId) {
        super(name, departmentId, courseId);
    }
}
