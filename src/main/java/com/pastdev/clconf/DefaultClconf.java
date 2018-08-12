package com.pastdev.clconf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

@Slf4j
public class DefaultClconf implements Clconf {
    private Map<String, String> environment = System.getenv();

    private <T> T[] combineArrays(T[] first, T[] second) {
        T[] combined = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, combined, first.length, second.length);
        return combined;
    }

    @Override
    public Object getValue(Map<String, Object> configuration, String path) {
        if (path == null || "".equals(path) || PATH_SEPARATOR.equals(path)) {
            return configuration;
        }

        String currentPath = PATH_SEPARATOR;
        Object value = configuration;
        for (String part : PATTERN_PATH_SPLITTER.split(path)) {
            if ("".equals(part)) {
                continue;
            }

            if (!(value instanceof Map)) {
                log.warn("value at {} not a map", currentPath);
                return null;
            }

            currentPath = (currentPath == PATH_SEPARATOR ? PATH_SEPARATOR
                    : currentPath + PATH_SEPARATOR) + part;

            @SuppressWarnings("unchecked")
            Map<String, Object> casted = (Map<String, Object>) value;
            value = casted.get(part);
            if (value == null) {
                log.warn("value at {} does not exist", currentPath);
                return null;
            }
        }

        return value;
    }

    @Override
    public Map<String, Object> setValue(Map<String, Object> configuration, String path,
            String value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> loadConfiguration(String[] files, String[] overrides)
            throws IOException {
        // streams don't play nice with checked exceptions
        // (https://stackoverflow.com/q/19757300/516433)
        // so lets do it the old fashioned way
        List<String> yamls = new ArrayList<>();
        if (files != null) {
            for (String file : files) {
                yamls.add(new String(Files.readAllBytes(Paths.get(file))));
            }
        }

        if (overrides != null) {
            yamls.addAll(Arrays.stream(overrides)
                    .map(base64 -> new String(Base64.getDecoder().decode(base64)))
                    .collect(Collectors.toList()));
        }

        return unmarshalYaml(yamls.toArray(new String[yamls.size()]));
    }

    @Override
    public Map<String, Object> loadConfigurationFromEnvironment(String[] files, String[] overrides)
            throws IOException {
        String yamlFiles = environment.get(ENV_YAML_FILES);
        if (yamlFiles != null) {
            files = combineArrays(files, PATTERN_SPLITTER.split(yamlFiles));
        }

        String yamlVars = environment.get(ENV_YAML_VARS);
        if (yamlVars != null) {
            overrides = combineArrays(overrides,
                    Arrays.stream(PATTERN_SPLITTER.split(yamlVars))
                            .map(name -> environment.get(name)).filter(var -> var != null)
                            .toArray(size -> new String[size]));
        }

        return loadConfiguration(files, overrides);
    }

    public void setEnvironment(Map<String, String> environment) {
        this.environment = environment;
    }

    @Override
    public String marshalYaml(Object value) {
        return new Yaml().dump(value);
    }

    @Override
    public Map<String, Object> unmarshalYaml(String... yamlStrings) {
        Yaml yaml = new Yaml();

        Map<String, Object> configuration = new HashMap<String, Object>();
        for (String yamlString : yamlStrings) {
            deepMerge(configuration, yaml.load(yamlString));
        }

        return configuration;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> castMap(Object object) {
        return (Map<String, Object>) object;
    }

    static Map<String, Object> deepMerge(Map<String, Object> configuration,
            Map<String, Object> overrides) {
        if (overrides instanceof Map && configuration instanceof Map) {
            for (String key : overrides.keySet()) {
                Object overridesValue = overrides.get(key);
                if (!configuration.containsKey(key)) {
                    configuration.put(key, overridesValue);
                }
                else {
                    Object configurationValue = configuration.get(key);
                    if (overridesValue instanceof Map) {
                        if (configurationValue instanceof Map) {
                            configuration.put(key,
                                    deepMerge(
                                            castMap(configurationValue),
                                            castMap(overridesValue)));
                        }
                    }
                    else {
                        configuration.put(key, overridesValue);
                    }
                }
            }

            return configuration;
        }

        return overrides;
    }
}
