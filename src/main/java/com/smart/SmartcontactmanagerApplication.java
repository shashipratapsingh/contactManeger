package com.smart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.smart.controller.UserController;

@SpringBootApplication
//@ComponentScan(basePackageClasses=UserController.class)
public class SmartcontactmanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartcontactmanagerApplication.class, args);
	}

}
