package com.pastdev.clconf.cli;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

@Slf4j
@Command(name = "getv", description = "Get a value")
public class Getv implements Callable<Void> {
	@Autowired
	private com.pastdev.clconf.Clconf clconf;
	@Mixin
	private GlobalOptions globalOptions;
	@Mixin
	private GetvParametersAndOptions parametersAndOptions;

	Object getValue() throws IOException {
		Map<String, Object> configuration = clconf.loadConfigurationFromEnvironment(globalOptions.getYaml(),
				globalOptions.getYamlBase64());
		if (parametersAndOptions.getPath() == null || parametersAndOptions.getPath().isEmpty()) {
			log.debug("getValue empty path");
			return configuration;
		}

		log.debug("getValue for path {}", parametersAndOptions.getPath());
		Object value = clconf.getValue(configuration, parametersAndOptions.getPath());

		if (value == null) {
			return parametersAndOptions.getDefaultValue() != null ? parametersAndOptions.getDefaultValue() : value;
		}

		return value;
	}

	@Override
	public Void call() throws Exception {
		System.out.println(clconf.marshalYaml(getValue()));
		return null;
	}
}
