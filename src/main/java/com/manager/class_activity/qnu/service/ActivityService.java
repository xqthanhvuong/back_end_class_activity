package com.manager.class_activity.qnu.service;


import com.manager.class_activity.qnu.dto.request.FileRequest;
import com.manager.class_activity.qnu.dto.response.ActivityResponse;
import com.manager.class_activity.qnu.dto.response.ActivitySummary;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.entity.Activity;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.repository.ActivityRepository;
import com.manager.class_activity.qnu.repository.ClassActivityRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActivityService {
    ActivityRepository activityRepository;
    ActivityGuideService activityGuideService;
    ClassActivityService classActivityService;

    @Transactional
    public void createClassActivity(List<FileRequest> files) {
        String currentMonthYear = java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("MM/yyyy"));
        Activity activity = activityRepository.findByNameAndIsDeleted(currentMonthYear, false);
        if (ObjectUtils.isNotEmpty(activity)) {
            throw new BadException(ErrorCode.ACTIVITY_EXIST);
        }
        activity = Activity.builder()
                .name(currentMonthYear)
                .build();
        activityRepository.save(activity);
        for (FileRequest file: files) {
            activityGuideService.createActivityGuide(file, activity);
        }
        classActivityService.createAllClassActivity(activity);

    }
    public Activity getActivityById(int id) {
        return activityRepository.findByIdAndIsDeleted(id,false).orElseThrow(
                ()->new BadException(ErrorCode.CLASS_ACTIVITY_NOT_FOUND)
        );
    }

    public PagedResponse<ActivityResponse> getActivities(CustomPageRequest<?> request) {
        Page<Activity> activities = activityRepository.findByPaged(
                request.toPageable(),
                request.getKeyWord()
        );
        List<ActivityResponse> rs = new ArrayList<>();
        for (Activity item: activities.getContent()) {
            rs.add(ActivityResponse.builder()
                            .createdAt(item.getCreatedAt())
                            .updatedAt(item.getUpdatedAt())
                            .id(item.getId())
                            .name(item.getName())
                    .build());
        }
        return new PagedResponse<>(
                rs,
                activities.getNumber(),
                activities.getTotalElements(),
                activities.getTotalPages(),
                activities.isLast()
        );
    }

    public List<ActivitySummary> getAllSummary() {
        List<Activity> activities = activityRepository.findByIsDeleted(false);
        List<ActivitySummary> rs = new ArrayList<>();
        for (Activity item: activities) {
            rs.add(ActivitySummary.builder()
                            .name(item.getName())
                            .id(item.getId())
                    .build());
        }
        return rs;
    }
}
