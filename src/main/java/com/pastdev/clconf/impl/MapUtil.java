package com.pastdev.clconf.impl;

import java.util.List;
import java.util.Map;

public class MapUtil {
    @SuppressWarnings("unchecked")
    public static List<Object> castList(Object object) {
        return (List<Object>) object;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> castMap(Object object) {
        return (Map<String, Object>) object;
    }

    /**
     * Merges <code>overrides</code> into <code>configuration</code> using a
     * recursive, depth first, traversal.  Either or both maps passed in may
     * have their contents modified, and a reference to one or the other may
     * be passed back.  No effort is made to make this safe to the arguments.
     * 
     * @param configuration The base map.
     * @param overrides The override values.
     * @return A merged map.
     */
    public static Map<String, Object> deepMerge(Map<String, Object> configuration,
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
