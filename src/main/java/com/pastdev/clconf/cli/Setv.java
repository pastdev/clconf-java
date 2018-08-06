package com.pastdev.clconf.cli;

import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

@Command(name = "setv", description = "Set <path> to <value> in the file indicated by the global option --yaml (must be single valued).")
public class Setv implements Callable<Void> {
	@Mixin
	private GlobalOptions globalOptions;

	@Mixin
	private SetvParametersAndOptions parametersAndOptions;

	@Override
	public Void call() throws Exception {
		return null;
	}

}
