package com.royce.hackerhouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HackerhouseApplication {

	public static void main(String[] args) {
		SpringApplication.run(HackerhouseApplication.class, args);
	}

}
