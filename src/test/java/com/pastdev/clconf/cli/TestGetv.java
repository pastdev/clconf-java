package com.pastdev.clconf.cli;

import static com.pastdev.clconf.TestUtil.assertYamlEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import picocli.CommandLine;
import picocli.CommandLine.Help;
import picocli.CommandLine.IFactory;

@RunWith(SpringRunner.class)
@SpringBootTest(
        properties = "spring.main.web-application-type=NONE",
        classes = {
                TestGetv.class,
                ClconfApplicationConfiguration.class,
        })
public class TestGetv {
    @Autowired
    private IFactory springFactory;

    private String clconf(String... args) throws UnsupportedEncodingException {
        PrintStream systemDotOut = System.out;
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try (PrintStream out = new PrintStream(stream, true, "UTF-8")) {
            System.setOut(out);
            CommandLine.call(Clconf.class, springFactory, out, Help.Ansi.AUTO, args);
        }
        finally {
            System.setOut(systemDotOut);
        }
        return new String(stream.toByteArray(), StandardCharsets.UTF_8);
    }

    private String base64(String yaml) throws UnsupportedEncodingException {
        return Base64.getEncoder().encodeToString(yaml.getBytes("utf-8"));
    }

    @Test
    public void getv() throws IOException {
        assertYamlEquals("one: 1", clconf("getv", "--yaml-base64", base64("one: 1")));
        assertYamlEquals("1", clconf("getv", "--yaml-base64", base64("one: 1"), "/one"));
    }
}
