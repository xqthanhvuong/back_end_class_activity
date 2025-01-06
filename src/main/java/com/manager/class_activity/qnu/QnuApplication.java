package com.manager.class_activity.qnu;

import com.manager.class_activity.qnu.entity.Class;
import com.manager.class_activity.qnu.service.ClassService;
import com.manager.class_activity.qnu.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@EnableAsync
@SpringBootApplication
@Slf4j
@EnableScheduling
@EnableTransactionManagement
public class QnuApplication {
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(QnuApplication.class, args);
		EmailService emailService = (EmailService) context.getBean("emailService");
//		try {
//			emailService.sendHtmlEmail("hoangtuquy775@gmail.com","mail mail", "<h1>Chào bạn!</h1><p>Cảm ơn bạn đã tham gia chúng tôi!</p>");
//		}catch (MessagingException e){
//			throw new RuntimeException(e);
//		}
	}
}
