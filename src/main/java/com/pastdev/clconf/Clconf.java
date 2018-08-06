package com.pastdev.clconf;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

public interface Clconf {
	/** Environment variable containing a comma separated list of file names */
	public static final String ENV_YAML_FILES = "YAML_FILES";
	/**
	 * Environment variable containing a comma separated list of environment 
	 * variable names whose values are base64 encoded yaml strings
	 */
	public static final String ENV_YAML_VARS = "YAML_VARS";
	/** The pattern used to split ENV_ variable values */
	public static final Pattern PATTERN_SPLITTER = Pattern.compile(",");

    Map<String, Object> getValue(Map<String, Object> configuration, String path);
    
    Map<String, Object> setValue(Map<String, Object> configuration, String path, String value);
    
    /**
     *  Loads all configurations present.  In order of precedence (highest last),
     *  files, overrides.
     *  
     * @param files A list of yaml files to merge
     * @param overrides A list of base64 encoded yaml strings
     * @return A multidimensional map of the merged configuration
     * @throws IOException if unable to read any of the files
     */
    Map<String, Object> loadConfiguration(String[] files, String[] overrides) throws IOException;
    
    /**
     *  Loads all configurations present.  In order of precedence (highest last),
     *  files, YAML_FILES env var, overrides, YAML_VARS env var.
     *  
     * @param files A list of yaml files to merge
     * @param overrides A list of base64 encoded yaml strings
     * @return A multidimensional map of the merged configuration
     * @throws IOException if unable to read any of the files
     */
    Map<String, Object> loadConfigurationFromEnvironment(String[] files, String[] overrides) throws IOException;
    
    Map<String, Object> unmarshalYaml(String... yaml);
}
