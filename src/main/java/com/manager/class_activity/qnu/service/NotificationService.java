package com.manager.class_activity.qnu.service;

import com.manager.class_activity.qnu.constant.EmailTemplateConstant;
import com.manager.class_activity.qnu.dto.response.NotificationResponse;
import com.manager.class_activity.qnu.dto.response.PagedResponse;
import com.manager.class_activity.qnu.entity.*;
import com.manager.class_activity.qnu.exception.BadException;
import com.manager.class_activity.qnu.exception.ErrorCode;
import com.manager.class_activity.qnu.helper.CustomPageRequest;
import com.manager.class_activity.qnu.repository.AccountRepository;
import com.manager.class_activity.qnu.repository.NotificationRecipientRepository;
import com.manager.class_activity.qnu.repository.NotificationRepository;
import com.manager.class_activity.qnu.repository.StudentRepository;
import com.manager.class_activity.qnu.socket.CustomWebSocketHandler;
import com.manager.class_activity.qnu.until.SecurityUtils;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
   NotificationRepository notificationRepository;
   NotificationRecipientRepository notificationRecipientRepository;
   StudentRepository studentRepository;
   AccountRepository accountRepository;
   CustomWebSocketHandler webSocketHandler;
   EmailService emailService;

   public Account getCurrentAccount() {
      String username = SecurityUtils.getCurrentUsername();
      return accountRepository.findByUsernameAndIsDeleted(username, false).orElseThrow(
              () -> new BadException(ErrorCode.USER_NOT_EXISTED)
      );
   }


   @Async
   @Transactional
   public void sendNotification(ClassActivity classActivity, String mess) {
      log.info("Sending notification for class activity ID: {}", classActivity.getId());
      List<Student> students = studentRepository.findByClazz_IdAndIsDeleted(classActivity.getClazz().getId(), false);
      if (students.isEmpty()) {
         log.warn("No students found for class activity ID: {}", classActivity.getId());
         return;
      }

      Notification notification = Notification.builder()
              .message(mess)
              .classActivity(classActivity)
              .build();
      notificationRepository.save(notification);

      for (Student student : students) {
         NotificationRecipient notificationRecipient = NotificationRecipient.builder()
                 .isRead(false)
                 .notification(notification)
                 .recipientType(NotificationRecipientEnum.Student)
                 .account(student.getAccount())
                 .build();
         notificationRecipientRepository.save(notificationRecipient);
         String userName = student.getAccount().getUsername();
         webSocketHandler.sendMessageToUser(userName, mess);
      }
      log.info("Notification sent successfully for class activity ID: {}", classActivity.getId());
//      sendEmailNotification(classActivity);
   }

   @Async
   public void sendEmailNotification(ClassActivity classActivity) {
      List<Student> students = studentRepository.findByClazz_IdAndIsDeleted(classActivity.getClazz().getId(), false);
      for (Student student : students) {
         if(ObjectUtils.isNotEmpty(student.getEmail())){
            try {
               emailService.sendHtmlEmail(student.getEmail(), "Notification",EmailTemplateConstant.generateNotificationTemplate(student.getName(),classActivity.getActivityTime().toString()));
            } catch (MessagingException e) {
               throw new RuntimeException(e);
            }
         }
      }
   }

   @Transactional
   public void setIsReadAll() {
      Account account = getCurrentAccount();
      log.info("Marking all notifications as read for account ID: {}", account.getId());

      List<NotificationRecipient> notificationRecipients = notificationRecipientRepository.findByAccountAndIsRead(account, false);
      if (notificationRecipients.isEmpty()) {
         log.info("No unread notifications found for account ID: {}", account.getId());
         return;
      }

      for (NotificationRecipient item : notificationRecipients) {
         item.setRead(true);
      }
      notificationRecipientRepository.saveAll(notificationRecipients);
      log.info("All notifications marked as read for account ID: {}", account.getId());
   }

   public PagedResponse<NotificationResponse> getNotification(CustomPageRequest<?> request) {
      Account account = getCurrentAccount();
      log.info("Fetching notifications for account ID: {}", account.getId());

      Page<NotificationResponse> notificationResponsePage = notificationRecipientRepository.getPageNotification(
              request.toPageable(), account.getId()
      );

      return new PagedResponse<>(
              notificationResponsePage.getContent(),
              notificationResponsePage.getNumber(),
              notificationResponsePage.getTotalElements(),
              notificationResponsePage.getTotalPages(),
              notificationResponsePage.isLast()
      );
   }

   public Integer countNotificationsNotYetRead() {
      Account account = getCurrentAccount();
      List<NotificationRecipient> notificationRecipients = notificationRecipientRepository.findByAccountAndIsRead(account, false);
      return notificationRecipients.size();
   }
}
