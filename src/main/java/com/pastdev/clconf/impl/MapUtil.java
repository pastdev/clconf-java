package com.pastdev.clconf.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pastdev.clconf.InvalidPathException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MapUtil {
    /** The path separator. */
    public static final String DEFAULT_PATH_SEPARATOR = "/";

    @SuppressWarnings("unchecked")
    public static List<Object> castList(Object object) {
        return (List<Object>) object;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> castMap(Object object) {
        return (Map<String, Object>) object;
    }

    /**
     * Merges <code>overrides</code> into <code>configuration</code> using a recursive, depth first,
     * traversal. Either or both maps passed in may have their contents modified, and a reference to
     * one or the other may be passed back. No effort is made to make this safe to the arguments.
     * 
     * @param configuration
     *            The base map.
     * @param overrides
     *            The override values.
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

    /**
     * Returns the value from <code>map</code> at <code>path</code>. Returns null if
     * <code>path</code> does not exist.
     * 
     * @param map
     *            A map of values
     * @param path
     *            The path to a value
     * @return The value at path from map, or null
     */
    public static Object getValueAt(Map<String, Object> map, String path) {
        return getValueAt(map, path, DEFAULT_PATH_SEPARATOR);
    }

    /**
     * Returns the value from <code>map</code> at <code>path</code> using the specified
     * <code>pathSeparator</code>. Returns null if <code>path</code> does not exist.
     * 
     * @param map
     *            A map of values
     * @param path
     *            The path to a value
     * @param pathSeparator
     *            The path segment separator
     * @return The value at path from map, or null
     */
    public static Object getValueAt(Map<String, Object> map, String path,
            String pathSeparator) {
        MapPath mapPath = new MapPath(path, pathSeparator);
        if (mapPath.empty()) {
            return map;
        }

        return getValueAt(map, mapPath.parts(), pathSeparator, false);
    }

    private static Object getValueAt(Map<String, Object> map, String[] parts, String pathSeparator,
            boolean create) {
        List<String> currentPath = new ArrayList<>();
        Object value = map;
        for (String part : parts) {
            if (!(value instanceof Map)) {
                throw new InvalidPathException("not a map", currentPath, pathSeparator);
            }
            Map<String, Object> currentMap = castMap(value);

            currentPath.add(part);
            
            if (!currentMap.containsKey(part)) {
                if (create) {
                    log.trace("value at {} is null, creating map", currentPath);
                    currentMap.put(part, new HashMap<String, Object>());
                }
                else {
                    throw new InvalidPathException("undefined", currentPath, pathSeparator);
                }
            }

            value = currentMap.get(part);
        }

        return value;
    }

    public static Map<String, Object> setValueAt(Map<String, Object> map, String path,
            Object value) {
        return setValueAt(map, path, DEFAULT_PATH_SEPARATOR, value);
    }

    /**
     * Sets the value in <code>map</code> at <code>path</code> to <code>value</code> using the
     * specified separator. This method modifies, and returns the map that was passed in.
     * 
     * @param map
     *            The map
     * @param path
     *            The path to set the value of
     * @param pathSeparator
     *            The path separator
     * @param value
     *            The value
     * @return The original map with the new value.
     */
    public static Map<String, Object> setValueAt(Map<String, Object> map, String path,
            String pathSeparator, Object value) {
        MapPath mapPath = new MapPath(path, pathSeparator);
        if (mapPath.empty()) {
            throw new InvalidPathException("cannot set root element", mapPath.parts(),
                    pathSeparator);
        }

        Object parent = getValueAt(map, mapPath.parentParts(), pathSeparator, true);
        if (!(parent instanceof Map)) {
            throw new InvalidPathException("not a map", mapPath.parentParts(), pathSeparator);
        }
        castMap(parent).put(mapPath.key(), value);

        return map;
    }

    static class MapPath {
        private String[] parts;

        MapPath(String path, String pathSeparator) {
            if (path == null || "".equals(path) || pathSeparator.equals(path)) {
                parts = new String[0];
            }
            else {
                parts = Arrays
                        .stream(path.split(pathSeparator))
                        .filter(part -> part.length() > 0)
                        .toArray(String[]::new);
            }
        }

        public boolean empty() {
            return parts.length == 0;
        }

        public String key() {
            return parts[parts.length - 1];
        }

        public String[] parentParts() {
            return Arrays.copyOfRange(parts, 0, parts.length - 1);
        }

        public String[] parts() {
            return parts;
        }
    }
}
