package com.pastdev.clconf.cli;

import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;

import com.pastdev.clconf.ClconfFactory;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

@Command(name = "getv", description = "Get a value")
public class Getv implements Callable<Void> {
	@Autowired
	private ClconfFactory clconfFactory;
	
	@Mixin
	private GetvParametersAndOptions parametersAndOptions;

	@Mixin
	private GlobalOptions globalOptions;

	@Override
	public Void call() throws Exception {
		com.pastdev.clconf.Clconf clconf = clconfFactory.NewClconf();
		Map<String, Object> configuration =
				clconf.loadConfigurationFromEnvironment(
						globalOptions.yaml.split(","),
						globalOptions.yamlBase64.split(","));
					
		return null;
	}
}
