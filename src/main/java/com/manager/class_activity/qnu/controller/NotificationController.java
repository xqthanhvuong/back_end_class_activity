package com.manager.class_activity.qnu.controller;

import com.manager.class_activity.qnu.dto.response.JsonResponse;
import com.manager.class_activity.qnu.dto.response.NotificationResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Endpoint để đánh dấu tất cả thông báo là đã đọc
     *
     * @return ResponseEntity<String>
     */
    @PutMapping("/read-all")
    public JsonResponse<String> markAllAsRead() {
        log.info("Received request to mark all notifications as read");
        notificationService.setIsReadAll();
        return JsonResponse.success("All notifications marked as read");
    }

    /**
     * Endpoint để lấy danh sách thông báo của người dùng
     *
     * @param request CustomPageRequest
     * @return ResponseEntity<PagedResponse<NotificationResponse>>
     */
    @PostMapping("/get-notifications")
    public JsonResponse<PagedResponse<NotificationResponse>> getNotifications(@RequestBody CustomPageRequest<Void> request) {
        log.info("Fetching notifications with page: {}, size: {}, sortBy: {}, direction: {}",
                request.getPage(), request.getSize(), request.getSortBy(), request.getDirection());

        PagedResponse<NotificationResponse> notifications = notificationService.getNotification(request);
        return JsonResponse.success(notifications);
    }

    @GetMapping("/count")
    public JsonResponse<Integer> countNotifications() {
        return JsonResponse.success(notificationService.countNotificationsNotYetRead());
    }
}
