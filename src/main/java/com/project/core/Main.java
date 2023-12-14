package com.project.core;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

	public static void main(String[] args) {

		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));

		System.out.println(" CORE is running ... ");
		SpringApplication.run(Main.class, args);
	}

}