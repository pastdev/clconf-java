package com.pastdev.clconf.cli;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

@SpringBootApplication
public class ClconfApplication implements CommandLineRunner {
    @Autowired
    private IFactory springFactory;

    public static void main(String[] args) {
        SpringApplication.run(ClconfApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        CommandLine.call(Clconf.class, springFactory, args);
    }
}
