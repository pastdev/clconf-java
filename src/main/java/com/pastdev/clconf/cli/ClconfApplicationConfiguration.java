package com.pastdev.clconf.cli;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import picocli.CommandLine.IFactory;

@Configuration
public class ClconfApplicationConfiguration {
    @Bean
    public static IFactory springFactory() {
        return new SpringFactory();
    }

    @Bean
    public static com.pastdev.clconf.Clconf clconf() {
        return new com.pastdev.clconf.DefaultClconf();
    }

    @Bean
    public static Clconf cli() {
        return new Clconf();
    }

    @Bean
    public static Getv getv() {
        return new Getv();
    }

    @Bean
    public static GetvParametersAndOptions getvParametersAndOptions() {
        return new GetvParametersAndOptions();
    }

    @Bean
    public static GlobalOptions globalOptions() {
        return new GlobalOptions();
    }

    @Bean
    public static Setv setv() {
        return new Setv();
    }

    @Bean
    public static SetvParametersAndOptions setvParametersAndOptions() {
        return new SetvParametersAndOptions();
    }
}
