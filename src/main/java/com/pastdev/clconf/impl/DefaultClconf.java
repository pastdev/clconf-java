package com.pastdev.clconf.impl;

import static com.pastdev.clconf.impl.MapUtil.deepMerge;
import static com.pastdev.clconf.impl.MapUtil.getValueAt;
import static com.pastdev.clconf.impl.MapUtil.setValueAt;

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

import com.pastdev.clconf.Clconf;
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
        return getValueAt(configuration, path);
    }

    @Override
    public Map<String, Object> setValue(Map<String, Object> configuration, String path,
            String value) {
        return setValueAt(configuration, path, value);
    }

    @Override
    public Map<String, Object> loadConfiguration(String[] files, String[] overrides)
            throws IOException {
        log.debug("loadConfiguration from {} files and {} overrides",
                files == null ? 0 : files.length, overrides == null ? 0 : overrides.length);

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
}
