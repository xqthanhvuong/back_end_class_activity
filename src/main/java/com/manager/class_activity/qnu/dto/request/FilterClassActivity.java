package com.manager.class_activity.qnu.dto.request;

import com.manager.class_activity.qnu.entity.Status;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilterClassActivity extends FilterStudent{
    Integer activityId;
    List<Status> activityStatus;
    public FilterClassActivity(String name, Integer departmentId, Integer courseId) {
        super(name, departmentId, courseId);
    }
}
