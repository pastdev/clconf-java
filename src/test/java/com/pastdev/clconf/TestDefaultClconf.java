package com.pastdev.clconf;

import static com.pastdev.clconf.DefaultClconf.deepMerge;
import static com.pastdev.clconf.TestUtil.assertDeepEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestDefaultClconf {
    @Test
    public void deepMergeBasic() {
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("foo", "bar");
        overrides.put("hip", "hop");

        Map<String, Object> configuration = new HashMap<>();
        configuration.put("foo", "baz");
        configuration.put("yip", "yap");

        Map<String, Object> expected = new HashMap<>();
        expected.put("foo", "bar");
        expected.put("hip", "hop");
        expected.put("yip", "yap");

        assertDeepEquals(expected, deepMerge(configuration, overrides));
    }

    @Test
    public void deepMergeNested() {
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("foo", "bar");
        overrides.put("hip", "hop");
        Map<String, Object> nested = new HashMap<>();
        nested.put("nestedfoo", "nestedbaz");
        nested.put("nestedhip", "nestedhop");
        overrides.put("nested", nested);
        overrides.put("list", Arrays.asList("one", "two", "three"));

        Map<String, Object> configuration = new HashMap<>();
        configuration.put("foo", "baz");
        configuration.put("yip", "yap");
        nested = new HashMap<>();
        nested.put("nestedfoo", "nestedbar");
        nested.put("nestedzip", "nestedzap");
        configuration.put("nested", nested);

        Map<String, Object> expected = new HashMap<>();
        expected.put("foo", "bar");
        expected.put("hip", "hop");
        expected.put("yip", "yap");
        nested = new HashMap<>();
        nested.put("nestedfoo", "nestedbaz");
        nested.put("nestedhip", "nestedhop");
        nested.put("nestedzip", "nestedzap");
        expected.put("nested", nested);
        expected.put("list", Arrays.asList("one", "two", "three"));

        assertDeepEquals(expected, deepMerge(configuration, overrides));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getValue() {
        Map<String, Object> configuration = new HashMap<>();
        configuration.put("one", 1);
        Map<String, Object> sub = new HashMap<>();
        sub.put("subone", 1);
        sub.put("subtwo", 2);
        configuration.put("sub", sub);

        assertEquals(1, new DefaultClconf().getValue(configuration, "one"));
        assertEquals(null, new DefaultClconf().getValue(configuration, "two"));

        Object actual = new DefaultClconf().getValue(configuration, null);
        assertDeepEquals(configuration, (Map<String, Object>) actual);
        actual = new DefaultClconf().getValue(configuration, "");
        assertDeepEquals(configuration, (Map<String, Object>) actual);
        actual = new DefaultClconf().getValue(configuration, "/");
        assertDeepEquals(configuration, (Map<String, Object>) actual);

        actual = new DefaultClconf().getValue(configuration, "/sub");
        assertTrue(actual instanceof Map);
        assertDeepEquals(sub, (Map<String, Object>) actual);
        assertEquals(1, new DefaultClconf().getValue(configuration, "/sub/subone"));
    }

    @Test
    public void loadConfiguration() {
        Map<String, Object> expected = new HashMap<>();
        expected.put("one", 1);
        expected.put("two", 2);
        expected.put("three", 3);
        expected.put("four", 4);

        try (TempYaml one = new TempYaml("one: 1");
                TempYaml two = new TempYaml("two: 2")) {
            assertDeepEquals(expected,
                    new DefaultClconf().loadConfiguration(
                            new String[] {
                                    one.toString(),
                                    two.toString(),
                            },
                            new String[] {
                                    Base64.getEncoder().encodeToString("three: 3".getBytes()),
                                    Base64.getEncoder().encodeToString("four: 4".getBytes()),
                            }));
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void loadConfigurationUsingFiles() {
        Map<String, Object> expected = new HashMap<>();
        expected.put("one", 1);
        expected.put("two", 2);

        try (TempYaml one = new TempYaml("one: 1");
                TempYaml two = new TempYaml("two: 2")) {
            assertDeepEquals(expected,
                    new DefaultClconf().loadConfiguration(
                            new String[] {
                                    one.toString(),
                                    two.toString(),
                            },
                            null));
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void loadConfigurationUsingOverrides() throws IOException {
        Map<String, Object> expected = new HashMap<>();
        expected.put("one", 1);
        expected.put("two", 2);

        assertDeepEquals(expected,
                new DefaultClconf().loadConfiguration(
                        null,
                        new String[] {
                                Base64.getEncoder().encodeToString("one: 1".getBytes()),
                                Base64.getEncoder().encodeToString("two: 2".getBytes()),
                        }));
    }

    @Test
    public void loadConfigurationFromEnvironment() {
        Map<String, Object> expected = new HashMap<>();
        expected.put("one", 1);
        expected.put("two", 2);
        expected.put("three", 3);
        expected.put("four", 4);
        expected.put("five", 5);
        expected.put("six", 6);
        expected.put("seven", 7);
        expected.put("eight", 8);

        try (TempYaml one = new TempYaml("one: 1");
                TempYaml two = new TempYaml("two: 2");
                TempYaml three = new TempYaml("three: 3");
                TempYaml four = new TempYaml("four: 4")) {

            final DefaultClconf clconf = new DefaultClconf();
            Map<String, String> environment = new HashMap<>();
            environment.put(Clconf.ENV_YAML_FILES, three.toString() + "," + four.toString());
            environment.put(Clconf.ENV_YAML_VARS, "FIVE_YAML,SIX_YAML");
            environment.put("FIVE_YAML", Base64.getEncoder().encodeToString("five: 5".getBytes()));
            environment.put("SIX_YAML", Base64.getEncoder().encodeToString("six: 6".getBytes()));
            clconf.setEnvironment(environment);

            assertDeepEquals(expected,
                    clconf.loadConfigurationFromEnvironment(
                            new String[] {
                                    one.toString(),
                                    two.toString(),
                            },
                            new String[] {
                                    Base64.getEncoder().encodeToString("seven: 7".getBytes()),
                                    Base64.getEncoder().encodeToString("eight: 8".getBytes()),
                            }));
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void unmarshalYamlMultiple() {
        final String original = "---\n"
                + "foo: baz\n"
                + "yip: yap\n"
                + "nested:\n"
                + "  nestedfoo: nestedbar\n"
                + "  nestedzip: nestedzap\n"
                + "list:\n"
                + "- one\n"
                + "- three\n";
        final String override = "---\n"
                + "foo: bar\n"
                + "hip: hop\n"
                + "nested:\n"
                + "  nestedfoo: nestedbaz\n"
                + "  nestedhip: nestedhop\n";
        final String overrideTwo = "---\n"
                + "list:\n"
                + "- one\n"
                + "- two\n"
                + "- three";

        Map<String, Object> expected = new HashMap<>();
        expected.put("foo", "bar");
        expected.put("hip", "hop");
        expected.put("yip", "yap");
        Map<String, Object> nested = new HashMap<>();
        nested.put("nestedfoo", "nestedbaz");
        nested.put("nestedhip", "nestedhop");
        nested.put("nestedzip", "nestedzap");
        expected.put("nested", nested);
        expected.put("list", Arrays.asList("one", "two", "three"));

        assertDeepEquals(expected,
                new DefaultClconf().unmarshalYaml(original, override, overrideTwo));
    }

    @Test
    public void unmarshalYamlSingle() {
        final String original = "---\n"
                + "foo: bar\n"
                + "hip: hop\n"
                + "yip: yap\n"
                + "nested:\n"
                + "  nestedfoo: nestedbaz\n"
                + "  nestedhip: nestedhop\n"
                + "  nestedzip: nestedzap\n"
                + "list:\n"
                + "- one\n"
                + "- two\n"
                + "- three";

        Map<String, Object> expected = new HashMap<>();
        expected.put("foo", "bar");
        expected.put("hip", "hop");
        expected.put("yip", "yap");
        Map<String, Object> nested = new HashMap<>();
        nested.put("nestedfoo", "nestedbaz");
        nested.put("nestedhip", "nestedhop");
        nested.put("nestedzip", "nestedzap");
        expected.put("nested", nested);
        expected.put("list", Arrays.asList("one", "two", "three"));

        assertDeepEquals(expected, new DefaultClconf().unmarshalYaml(original));
    }
}
