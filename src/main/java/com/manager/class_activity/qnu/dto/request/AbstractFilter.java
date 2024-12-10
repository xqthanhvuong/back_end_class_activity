package com.manager.class_activity.qnu.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class AbstractFilter {
    String keyWord;

    public Integer getDepartmentId() {
        return null;  // Trả về null mặc định cho lớp không có departmentId
    }

    public Integer getCourseId() {
        return null;  // Trả về null mặc định cho lớp không có courseId
    }

    public Integer getClassId(){
        return null;
    }

    public Integer getActivityId(){
        return null;
    }
}
