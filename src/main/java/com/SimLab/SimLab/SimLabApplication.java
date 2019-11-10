package com.SimLab.SimLab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan("com.DAO")
@SpringBootApplication
public class SimLabApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimLabApplication.class, args);
	}

}
