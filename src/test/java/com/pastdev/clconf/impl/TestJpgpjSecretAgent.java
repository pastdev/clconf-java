package com.pastdev.clconf.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import com.pastdev.clconf.SecretAgent;
import org.bouncycastle.openpgp.PGPException;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestJpgpjSecretAgent {
    private static SecretAgent secretAgent;

    /**
     * Initializes the secret agent.
     */
    @BeforeClass
    public static void initializeClass() throws IOException, PGPException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                TestJpgpjSecretAgent.class
                        .getClassLoader()
                        .getResourceAsStream("test.secring.gpg")))) {
            secretAgent = new JpgpjSecretAgent(
                    new DefaultClconf(),
                    reader.lines().collect(Collectors.joining("\n")));
        }
    }

    @Test
    public void encryptDecrypt() throws IOException {
        String expected = "foobar";

        String actual = secretAgent.encrypt(expected);
        assertNotEquals(expected, actual);

        actual = secretAgent.decrypt(actual);
        assertEquals(expected, actual);
    }
}
