package com.manager.class_activity.qnu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class QnuApplication {
	public static void main(String[] args) {
		SpringApplication.run(QnuApplication.class, args);
	}
}
