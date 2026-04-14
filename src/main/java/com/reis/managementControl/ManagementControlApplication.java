package com.reis.managementControl;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

import javafx.application.Application;

@SpringBootApplication
@Profile("prod")
public class ManagementControlApplication {

	public static void main(String[] args) {
		Application.launch(ManagementApp.class, args);
	}

}
