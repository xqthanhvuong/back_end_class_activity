package com.manager.class_activity.qnu.mapper;

import com.manager.class_activity.qnu.dto.response.ActivityGuideResponse;
import com.manager.class_activity.qnu.dto.response.ActivityGuideSummary;
import com.manager.class_activity.qnu.entity.ActivityGuide;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityGuideMapper {
    ActivityGuideResponse toActivityGuideResponse(ActivityGuide activityGuide);

    ActivityGuideSummary toSummaryActivityGuide(ActivityGuide a);
}
