package com.manager.class_activity.qnu.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MinutesActivityRequest {
    String lastMonthActivity;
    String thisMonthActivity;
    String teacherFeedback;
    String classFeedback;
    int classActivityId;
    Integer id;
}
