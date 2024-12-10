package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.dto.request.FileRequest;
import com.manager.class_activity.qnu.dto.request.Filter;
import com.manager.class_activity.qnu.dto.response.GuideResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.entity.Activity;
import com.manager.class_activity.qnu.entity.ActivityGuide;
import com.manager.class_activity.qnu.entity.Department;
import com.manager.class_activity.qnu.entity.DepartmentActivityGuide;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.repository.DepartmentActivityGuideRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class DepartmentGuideService {
    DepartmentActivityGuideRepository departmentActivityGuideRepository;
    CloudinaryService cloudinaryService;
    AccountService accountService;

    public void addDepartmentActivityGuide(FileRequest request, Activity activity) {
        if (request.getFile() == null || request.getFile().isEmpty()) {
            throw new BadException(ErrorCode.INVALID_FILE);
        }

        try {
            String pdfUrl = cloudinaryService.uploadPdfAsync(request.getFile()).get();
            Department department = accountService.getDepartmentOfAccount();
            DepartmentActivityGuide departmentActivityGuide = DepartmentActivityGuide.builder()
                    .activity(activity)
                    .name(request.getName())
                    .department(department)
                    .pdfUrl(pdfUrl)
                    .build();
            departmentActivityGuideRepository.save(departmentActivityGuide);
        } catch (InterruptedException | ExecutionException e) {
            // Log lỗi và ném ngoại lệ
            e.printStackTrace();
            throw new BadException(ErrorCode.UPLOAD_ERROR);
        }

    }

    public void deleteGuide(int id) {
        DepartmentActivityGuide departmentActivityGuide = departmentActivityGuideRepository
                .findByIdAndIsDeleted(id, false).orElseThrow(
                        ()-> new BadException(ErrorCode.DOCUMENT_NOT_FOUND)
                );
        departmentActivityGuide.setDeleted(true);
        departmentActivityGuideRepository.save(departmentActivityGuide);
    }

    public PagedResponse<GuideResponse> getActivitiesByPage(CustomPageRequest<Filter> request) {
        Department department = accountService.getDepartmentOfAccount();
        Page<ActivityGuide> activityGuidePage = departmentActivityGuideRepository.getBypage(
                request.toPageable(),
                request.getKeyWord(),
                department.getId()
        );
        List<GuideResponse> rs = new ArrayList<>();
        for (ActivityGuide item: activityGuidePage.getContent()) {
            rs.add(GuideResponse.builder()
                    .updatedAt(item.getUpdatedAt())
                    .createdAt(item.getCreatedAt())
                    .id(item.getId())
                    .isDepartment(true)
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
}
