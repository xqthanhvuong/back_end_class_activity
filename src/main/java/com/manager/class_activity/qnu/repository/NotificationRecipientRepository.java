package com.manager.class_activity.qnu.repository;

import com.manager.class_activity.qnu.dto.response.NotificationResponse;
import com.manager.class_activity.qnu.entity.Account;
import com.manager.class_activity.qnu.entity.NotificationRecipient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, Integer> {
    List<NotificationRecipient> findByAccountAndIsRead(Account account, boolean b);

    @Query("SELECT new com.manager.class_activity.qnu.dto.response.NotificationResponse" +
            "(n.message, n.classActivity.id, n.notificationTime, " +
            "nr.recipientType, nr.isRead) " +
            "FROM NotificationRecipient nr " +
            "JOIN nr.notification n " + 
            "WHERE nr.account.id = :accountId " +
            "ORDER BY n.notificationTime DESC")
    Page<NotificationResponse> getPageNotification(Pageable pageable, Integer accountId);

}
