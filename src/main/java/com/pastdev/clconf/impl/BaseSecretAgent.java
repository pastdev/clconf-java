package com.pastdev.clconf.impl;

import java.io.IOException;
import java.util.Map;

import com.pastdev.clconf.Clconf;
import com.pastdev.clconf.SecretAgent;
import org.bouncycastle.openpgp.PGPException;

public abstract class BaseSecretAgent implements SecretAgent {
    private Clconf clconf;

    public BaseSecretAgent(Clconf clconf) throws IOException, PGPException {
        this.clconf = clconf;
    }

    @Override
    public void decryptPaths(Map<String, Object> configuration, String... paths)
            throws IOException {
        for (String path : paths) {
            Object value = clconf.getValue(configuration, path);
            if (!(value instanceof String)) {
                throw new IllegalArgumentException(""
                        + "value at ["
                        + path + "] "
                        + "is not a ciphertext");
            }
            
            clconf.setValue(configuration, path, encrypt((String)value));
        }
    }
}
