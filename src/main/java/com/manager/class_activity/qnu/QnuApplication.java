package com.manager.class_activity.qnu;

import com.manager.class_activity.qnu.entity.Class;
import com.manager.class_activity.qnu.service.ClassService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@SpringBootApplication
@Slf4j
@EnableTransactionManagement
public class QnuApplication {
	public static void main(String[] args) {
		SpringApplication.run(QnuApplication.class, args);
	}
}
