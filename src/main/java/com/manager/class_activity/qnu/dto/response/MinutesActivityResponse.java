package com.manager.class_activity.qnu.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MinutesActivityResponse {
    String lastMonthActivity;
    String thisMonthActivity;
    String teacherFeedback;
    String classFeedback;
    int classActivityId;
    Integer id;
}
