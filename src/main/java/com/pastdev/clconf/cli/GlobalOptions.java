package com.pastdev.clconf.cli;

import picocli.CommandLine.Option;

public class GlobalOptions {
	@Option(names = "--prefix", description = "Prepended to all getv/setv paths (env: CONFIG_PREFIX)" )
	String prefix;
	
	@Option(names = "--secret-keyring", description = "Path to a gpg secring file (env: SECRET_KEYRING)")
	String secretyKeyring;
	
	@Option(names = "--secret-keyring-base64", description = "Base64 encoded gpg secring (env: SECRET_KEYRING_BASE64)")
	String secretKeyringBase64;
	
	@Option(names = "--yaml", description = "A list of yaml files containing config (env: YAML_FILES).  If specified, YAML_FILES will be split on ',' and appended to this option.")
	String yaml;
	
	@Option(names = "--yaml-base64", description = "A list of base 64 encoded yaml strings containing config (env: YAML_VARS).  If specified, YAML_VARS will be split on ',' and each value will be used to load a base64 string from an environtment variable of that name.  The values will be appended to this option.")
	String yamlBase64;
}
