package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.entity.ActivityGuide;
import com.manager.class_activity.qnu.entity.ClassActivity;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.repository.ActivityGuideRepository;
import com.manager.class_activity.qnu.repository.ClassActivityRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActivityGuideService {
    ActivityGuideRepository activityGuideRepository;
    CloudinaryService cloudinaryService;
    ClassActivityRepository classActivityRepository;

    @Transactional(rollbackFor = Exception.class)
    public ActivityGuide createActivityGuide(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadException(ErrorCode.INVALID_FILE);
        }

        try {
            // Định nghĩa format tháng/năm
            String currentMonthYear = java.time.LocalDate.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("MM/yyyy"));
            ActivityGuide guide = activityGuideRepository.findByNameAndIsDeleted(currentMonthYear,false);
            if (ObjectUtils.isNotEmpty(guide)) {
                for (ClassActivity classActivity : guide.getClassActivities()) {
                    classActivity.setDeleted(true);
                    classActivityRepository.save(classActivity);
                }
                guide.setDeleted(true);
                activityGuideRepository.save(guide);
            }

            // Upload file lên Cloudinary
            String pdfUrl = cloudinaryService.uploadPdfAsync(file).get();

            // Tạo đối tượng ActivityGuide
            ActivityGuide activityGuide = ActivityGuide.builder()
                    .name(currentMonthYear)
                    .pdfUrl(pdfUrl)
                    .build();

            // Lưu đối tượng vào cơ sở dữ liệu
            activityGuideRepository.save(activityGuide);

            return activityGuide;
        } catch (InterruptedException | ExecutionException e) {
            // Log lỗi và ném ngoại lệ
            e.printStackTrace();
            throw new BadException(ErrorCode.UPLOAD_ERROR);
        }
    }


}
