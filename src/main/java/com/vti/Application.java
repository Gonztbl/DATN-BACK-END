package com.vti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@ComponentScan(excludeFilters = @ComponentScan.Filter(
    type = FilterType.ASSIGNABLE_TYPE,
    classes = com.vti.springdatajpa.service.CardWithdrawService.class
))
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
	