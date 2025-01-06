package com.manager.class_activity.qnu.dto.request;

import com.manager.class_activity.qnu.entity.Status;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class AbstractFilter {
    String keyWord;

    public Integer getDepartmentId() {
        return null;
    }

    public Integer getCourseId() {
        return null;
    }

    public Integer getClassId(){
        return null;
    }

    public Integer getActivityId(){
        return null;
    }

    public List<Status> getActivityStatus(){
        return null;
    }
}
