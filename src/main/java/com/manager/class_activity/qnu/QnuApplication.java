package com.manager.class_activity.qnu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@SpringBootApplication
@Slf4j
@EnableScheduling
@EnableTransactionManagement
public class QnuApplication {
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(QnuApplication.class, args);
	}
}
