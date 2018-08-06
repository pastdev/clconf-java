package com.pastdev.clconf.cli;

import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

public class GetvParametersAndOptions {
	@Parameters(index = "0", description = "yaml path")
	String path;

	@Option(names = "--decrypt", description = "A list of paths whose values needs to be decrypted")
	String decript;

	@Option(names = "--default", description = "The value to be returned if the specified path does not exist (otherwise results in an error).")
	String defaultValue;

	@Option(names = "--template", description = "A ? template file that will be executed against the resulting data.")
	String template;

	@Option(names = "--template-base64", description = "A base64 encoded string containing a ? template that will be executed against the resulting data.")
	String templateBase64;
	
	@Option(names = "--template-string", description = "A string containing a ? template that will be executed against the resulting data.")
	String templateString;
}
