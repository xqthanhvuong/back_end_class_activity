package com.manager.class_activity.qnu;

import com.manager.class_activity.qnu.entity.Class;
import com.manager.class_activity.qnu.service.ClassService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@Slf4j
public class QnuApplication {
	public static void main(String[] args) {
		ApplicationContext context= SpringApplication.run(QnuApplication.class, args);
		ClassService classService = context.getBean(ClassService.class);

		for (Class clazz : classService.getClasses()) {
			log.info(clazz.getName());
		}
	}
}
