package com.project.groceryTracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GroceryTrackerApplication {

	public static void main(String[] args) {

		SpringApplication.run(GroceryTrackerApplication.class, args);
	}

}
