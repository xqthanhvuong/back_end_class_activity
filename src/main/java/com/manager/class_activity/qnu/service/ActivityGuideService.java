package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.dto.request.FileRequest;
import com.manager.class_activity.qnu.dto.response.ActivityGuideSummary;
import com.manager.class_activity.qnu.dto.response.GuideResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.entity.*;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.mapper.ActivityGuideMapper;
import com.manager.class_activity.qnu.repository.ActivityGuideRepository;
import com.manager.class_activity.qnu.repository.ActivityRepository;
import com.manager.class_activity.qnu.repository.ClassActivityRepository;
import com.manager.class_activity.qnu.repository.DepartmentActivityGuideRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActivityGuideService {
    ActivityGuideRepository activityGuideRepository;
    CloudinaryService cloudinaryService;
    LocalStorageService localStorageService;
    ClassActivityRepository classActivityRepository;
    ActivityGuideMapper activityGuideMapper;
    DepartmentActivityGuideRepository departmentActivityGuideRepository;
    AccountService accountService;
    ActivityRepository activityRepository;

    @Transactional(rollbackFor = Exception.class)
    public void createActivityGuide(FileRequest request, Activity activity) {
        if (request.getFile() == null || request.getFile().isEmpty()) {
            throw new BadException(ErrorCode.INVALID_FILE);
        }

        try {
            // Upload file lên Cloudinary
            String pdfUrl = localStorageService.uploadPdfAsync(request.getFile()).get();

            // Tạo đối tượng ActivityGuide
            ActivityGuide activityGuide = ActivityGuide.builder()
                    .name(request.getName())
                    .activity(activity)
                    .pdfUrl(pdfUrl)
                    .build();

            // Lưu đối tượng vào cơ sở dữ liệu
            activityGuideRepository.save(activityGuide);

        } catch (InterruptedException | ExecutionException e) {
            // Log lỗi và ném ngoại lệ
            e.printStackTrace();
            throw new BadException(ErrorCode.UPLOAD_ERROR);
        }
    }

    public PagedResponse<GuideResponse> getActivitiesByPage(CustomPageRequest<?> request){
        Page<ActivityGuide> activityGuidePage = activityGuideRepository.getBypage(
                request.toPageable(),
                request.getKeyWord()
        );
        List<GuideResponse> rs = new ArrayList<>();
        for (ActivityGuide item: activityGuidePage.getContent()) {
            rs.add(GuideResponse.builder()
                    .updatedAt(item.getUpdatedAt())
                    .createdAt(item.getCreatedAt())
                    .id(item.getId())
                    .isDepartment(false)
                    .url(item.getPdfUrl())
                    .name(item.getName())
                    .build());
        }
        return new PagedResponse<>(
                rs,
                activityGuidePage.getNumber(),
                activityGuidePage.getTotalElements(),
                activityGuidePage.getTotalPages(),
                activityGuidePage.isLast()
        );
    }

    public List<ActivityGuideSummary> getAllSummary() {
        List<ActivityGuide> activityGuides = activityGuideRepository.findAllByIsDeleted(false);
        List<ActivityGuideSummary> rs = new ArrayList<>();
        for (ActivityGuide a: activityGuides) {
            rs.add(activityGuideMapper.toSummaryActivityGuide(a));
        }
        return rs;
    }

    public List<GuideResponse> getAllActivityGuides(int id) {
        List<GuideResponse> rs = new ArrayList<>();
        Activity activity = activityRepository.findByIdAndIsDeleted(id, false).orElseThrow(
                ()-> new BadException(ErrorCode.CLASS_ACTIVITY_NOT_FOUND)
        );
        List<ActivityGuide> activityGuide = activityGuideRepository.findByActivity_IdAndIsDeleted(activity.getId(),false);
        Department department = accountService.getDepartmentOfAccount();
        for (ActivityGuide item: activityGuide) {
            rs.add(GuideResponse.builder()
                            .updatedAt(item.getUpdatedAt())
                            .createdAt(item.getCreatedAt())
                            .id(item.getId())
                            .isDepartment(false)
                            .url(item.getPdfUrl())
                            .name(item.getName())
                    .build());
        }
        List<DepartmentActivityGuide> departmentActivityGuides = departmentActivityGuideRepository
                .findByActivityIdAndDepartmentId(activity.getId()
                        ,  ObjectUtils.isNotEmpty(department)? department.getId() : null);
        for (DepartmentActivityGuide de: departmentActivityGuides) {
            rs.add(GuideResponse.builder()
                            .id(de.getId())
                            .isDepartment(true)
                            .createdAt(de.getCreatedAt())
                            .updatedAt(de.getUpdatedAt())
                            .name(de.getName())
                            .url(de.getPdfUrl())
                    .build());
        }
        return rs;
    }


    public void deleteActivityGuide(int id) {
        ActivityGuide activity = activityGuideRepository.findByIdAndIsDeleted(id, false).orElseThrow(
                ()-> new BadException(ErrorCode.CLASS_ACTIVITY_NOT_FOUND)
        );
        activity.setDeleted(true);
        activityGuideRepository.save(activity);
    }
}
