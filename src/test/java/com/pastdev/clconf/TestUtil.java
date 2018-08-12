package com.pastdev.clconf;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class TestUtil {
    @SuppressWarnings("unchecked")
    private static List<Object> castList(Object object) {
        return (List<Object>) object;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> castMap(Object object) {
        return (Map<String, Object>) object;
    }

    /**
     * Asserts that the maps are equivalent. A recursive comparison is made 
     * using a depth first traversal.
     * 
     * @param expected The expected value
     * @param actual The actual value
     */
    public static void assertDeepEquals(Map<String, Object> expected, Map<String, Object> actual) {
        assertDeepEquals(expected, actual, "");
    }

    /**
     * Asserts that the maps are equivalent. A recursive comparison is made 
     * using a depth first traversal.
     * 
     * @param expected The expected value
     * @param actual The actual value
     * @param base A base message
     */
    public static void assertDeepEquals(Map<String, Object> expected, Map<String, Object> actual,
            String base) {
        assertEquals(base + " key count", expected.size(), actual.size());

        for (String key : expected.keySet()) {
            Object expectedValue = expected.get(key);
            Object actualValue = actual.get(key);

            if (expectedValue instanceof Map) {
                if (actualValue instanceof Map) {
                    assertDeepEquals(castMap(expectedValue), castMap(actualValue),
                            base + "/" + key);
                }
                else {
                    fail(base + "/" + key + " expected map, got " + actual.getClass().getName());
                }
            }
            else if (expectedValue instanceof List) {
                if (actualValue instanceof List) {
                    assertArrayEquals(base + "/" + key,
                            castList(expectedValue).toArray(),
                            castList(actualValue).toArray());
                }
                else {
                    fail(base + "/" + key + " expected list, got " + actual.getClass().getName());
                }
            }
            else {
                assertEquals(base + "/" + key, expectedValue, actualValue);
            }
        }
    }

    /**
     * Asserts that the yaml strings are equivalent. Each string will be
     * loaded into a java object and then compared.
     * 
     * @param expectedYaml The expected value
     * @param actualYaml The actual value
     */
    public static void assertYamlEquals(String expectedYaml, String actualYaml) {
        Yaml yaml = new Yaml();
        Object expected = yaml.load(expectedYaml);
        Object actual = yaml.load(actualYaml);

        if (expected instanceof Map) {
            assertTrue("expected map, got " + actual.getClass().getName(), actual instanceof Map);
            assertDeepEquals(castMap(expected), castMap(actual));
        }
        else if (expected instanceof List) {
            assertTrue("expected list, got " + actual.getClass().getName(), actual instanceof List);
            assertArrayEquals(castList(expected).toArray(), castList(actual).toArray());
        }
    }
}
