package com.pastdev.clconf.cli;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import picocli.CommandLine;

@SpringBootApplication
public class ClconfApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ClconfApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		CommandLine.call(new Clconf(), args);
	}
}
