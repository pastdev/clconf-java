package com.pastdev.clconf.cli;

import lombok.Data;
import lombok.NoArgsConstructor;
import picocli.CommandLine.Option;

@Data
@NoArgsConstructor(force = true)
public class GlobalOptions {
    @Option(names = "--prefix",
            description = "Prepended to all getv/setv paths (env: CONFIG_PREFIX)")
    private String prefix;

    @Option(names = "--secret-keyring",
            description = "Path to a gpg secring file (env: SECRET_KEYRING)")
    private String secretyKeyring;

    @Option(names = "--secret-keyring-base64",
            description = "Base64 encoded gpg secring (env: SECRET_KEYRING_BASE64)")
    private String secretKeyringBase64;

    @Option(names = "--yaml",
            description = ""
                    + "A list of yaml files containing config (env: YAML_FILES). "
                    + "If specified, YAML_FILES will be split on ',' and appended "
                    + "to this option.")
    private String yaml;

    @Option(names = "--yaml-base64",
            description = ""
                    + "A list of base 64 encoded yaml strings containing config "
                    + "(env: YAML_VARS). If specified, YAML_VARS will be split "
                    + "on ',' and each value will be used to load a base64 "
                    + "string from an environtment variable of that name. The "
                    + "values will be appended to this option.")
    private String yamlBase64;

    /**
     * Splits the value of <code>--yaml</code> and returns the list.
     * 
     * @return The list of yaml files.
     */
    public String[] getYaml() {
        if (yaml == null) {
            return new String[0];
        }
        return com.pastdev.clconf.Clconf.PATTERN_SPLITTER.split(yaml);
    }

    /**
     * Splits the value of <code>--yaml-base64</code> and returns the list.
     * 
     * @return The list of base64 encoded yaml content.
     */
    public String[] getYamlBase64() {
        if (yamlBase64 == null) {
            return new String[0];
        }
        return com.pastdev.clconf.Clconf.PATTERN_SPLITTER.split(yamlBase64);
    }
}
