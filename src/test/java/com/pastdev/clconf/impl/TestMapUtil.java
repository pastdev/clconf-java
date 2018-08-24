package com.pastdev.clconf.impl;


import static com.pastdev.clconf.impl.MapUtil.deepMerge;
import static com.pastdev.clconf.impl.TestUtil.assertDeepEquals;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


import org.junit.Test;


public class TestMapUtil {
    @Test
    public void deepMergeBasic() {
        Map<String, Object> overrides = new HashMap<>();
        overrides.put( "foo", "bar" );
        overrides.put( "hip", "hop" );

        Map<String, Object> configuration = new HashMap<>();
        configuration.put( "foo", "baz" );
        configuration.put( "yip", "yap" );

        Map<String, Object> expected = new HashMap<>();
        expected.put( "foo", "bar" );
        expected.put( "hip", "hop" );
        expected.put( "yip", "yap" );

        assertDeepEquals( expected, deepMerge( configuration, overrides ) );
    }

    @Test
    public void deepMergeNested() {
        Map<String, Object> overrides = new HashMap<>();
        overrides.put( "foo", "bar" );
        overrides.put( "hip", "hop" );
        Map<String, Object> nested = new HashMap<>();
        nested.put( "nestedfoo", "nestedbaz" );
        nested.put( "nestedhip", "nestedhop" );
        overrides.put( "nested", nested );
        overrides.put( "list", Arrays.asList( "one", "two", "three" ) );

        Map<String, Object> configuration = new HashMap<>();
        configuration.put( "foo", "baz" );
        configuration.put( "yip", "yap" );
        nested = new HashMap<>();
        nested.put( "nestedfoo", "nestedbar" );
        nested.put( "nestedzip", "nestedzap" );
        configuration.put( "nested", nested );

        Map<String, Object> expected = new HashMap<>();
        expected.put( "foo", "bar" );
        expected.put( "hip", "hop" );
        expected.put( "yip", "yap" );
        nested = new HashMap<>();
        nested.put( "nestedfoo", "nestedbaz" );
        nested.put( "nestedhip", "nestedhop" );
        nested.put( "nestedzip", "nestedzap" );
        expected.put( "nested", nested );
        expected.put( "list", Arrays.asList( "one", "two", "three" ) );

        assertDeepEquals( expected, deepMerge( configuration, overrides ) );
    }
}
