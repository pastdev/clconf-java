package com.pastdev.clconf;

import java.io.IOException;
import java.util.Map;

public interface SecretAgent {
    /**
     * Returns the decrypted value of <code>ciphertext</code>.
     * 
     * @param ciphertext
     *            The encrypted value.
     * @return The decrypted value.
     */
    String decrypt(String ciphertext) throws IOException;

    /**
     * Replaces each ciphertext in configuration represented by one of the <code>paths</code> with
     * its decrypted value.
     * 
     * @param configuration
     *            The configuration map.
     * @param paths
     *            A list of paths to decrypt.
     */
    void decryptPaths(Map<String, Object> configuration, String... paths) throws IOException;

    /**
     * Returns the encrypted value of <code>plaintext</code>.
     * 
     * @param plaintext
     *            The plaintext value.
     * @return The encrypted value.
     */
    String encrypt(String plaintext) throws IOException;
}
