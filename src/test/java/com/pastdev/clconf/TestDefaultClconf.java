package com.pastdev.clconf;

import static com.pastdev.clconf.DefaultClconf.deepMerge;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

public class TestDefaultClconf {
	private void assertDeepEquals(Map<String, Object> expected, Map<String, Object> actual) {
        assertDeepEquals(expected, actual, "");
	}

	private void assertDeepEquals(Map<String, Object> expected, Map<String, Object> actual, String base) {
	    assertEquals(base + " key count", expected.size(), actual.size());
	    
	    for (String key : expected.keySet()) {
	    	Object expectedValue = expected.get(key);
	    	Object actualValue = actual.get(key);

	    	if (expectedValue instanceof Map) {
	    		if (actualValue instanceof Map) {
	    			@SuppressWarnings("unchecked")
					Map<String, Object> castedExpectedValue = (Map<String, Object>)expectedValue;
	    			@SuppressWarnings("unchecked")
					Map<String, Object> castedActualValue = (Map<String, Object>)actualValue;
	    			assertDeepEquals(castedExpectedValue, castedActualValue, base + "/" + key);
	    		}
	    		else {
	    	        fail(base + "/" + key + " expected map, got " + actual.getClass().getName());
	    		}
	    	}
	    	else if (expectedValue instanceof List) {
	    		if (actualValue instanceof List) {
	    			@SuppressWarnings("unchecked")
					List<Object> castedExpectedValue = (List<Object>)expectedValue;
	    			@SuppressWarnings("unchecked")
					List<Object> castedActualValue = (List<Object>)actualValue;
	    			assertArrayEquals(base + "/" + key,
	    					castedExpectedValue.toArray(),
	    					castedActualValue.toArray());
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

	@Test
	public void loadConfiguration() {
		Map<String, Object> expected = new HashMap<>();
		expected.put("one", 1);
		expected.put("two", 2);
		expected.put("three", 3);
		expected.put("four", 4);

		try (TempYml one = new TempYml("one: 1");
		        TempYml two = new TempYml("two: 2")) {
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

		try (TempYml one = new TempYml("one: 1");
		        TempYml two = new TempYml("two: 2")) {
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

		try (TempYml one = new TempYml("one: 1");
		        TempYml two = new TempYml("two: 2");
		        TempYml three = new TempYml("three: 3");
		        TempYml four = new TempYml("four: 4")) {

			DefaultClconf clconf = new DefaultClconf();
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
		String original = "---\n" +
	            "foo: baz\n" +
	            "yip: yap\n" +
	            "nested:\n" +
	            "  nestedfoo: nestedbar\n" +
	            "  nestedzip: nestedzap\n" +
	            "list:\n" +
	            "- one\n" +
	            "- three\n";
		String override = "---\n" +
	            "foo: bar\n" +
	            "hip: hop\n" +
	            "nested:\n" +
	            "  nestedfoo: nestedbaz\n" +
	            "  nestedhip: nestedhop\n";
		String overrideTwo = "---\n" +
	            "list:\n" +
	            "- one\n" +
	            "- two\n" +
	            "- three";

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

		assertDeepEquals(expected, new DefaultClconf().unmarshalYaml(original, override, overrideTwo));
	}
	
	@Test
	public void unmarshalYamlSingle() {
		String original = "---\n" +
	            "foo: bar\n" +
	            "hip: hop\n" +
	            "yip: yap\n" +
	            "nested:\n" +
	            "  nestedfoo: nestedbaz\n" +
	            "  nestedhip: nestedhop\n" +
	            "  nestedzip: nestedzap\n" +
	            "list:\n" +
	            "- one\n" +
	            "- two\n" +
	            "- three";

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
	
	private class TempYml implements Closeable {
		private Path path;
		
		private TempYml(String content) throws IOException {
			path = Files.createTempFile(UUID.randomUUID().toString(), ".yml");
			Files.write(path, content.getBytes());
		}

		@Override
		public void close() throws IOException {
			if (Files.exists(path)) {
				Files.delete(path);
			}
		}
		
		@Override
		public String toString() {
			return path.toString();
		}
	}
}
