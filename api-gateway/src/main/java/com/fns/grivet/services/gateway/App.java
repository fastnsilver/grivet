package com.fns.grivet.services.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.sidecar.EnableSidecar;

@SpringBootApplication
@EnableSidecar
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
