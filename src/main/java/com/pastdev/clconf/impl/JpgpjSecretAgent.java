package com.pastdev.clconf.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import com.pastdev.clconf.Clconf;
import org.bouncycastle.openpgp.PGPException;
import org.c02e.jpgpj.Decryptor;
import org.c02e.jpgpj.Encryptor;
import org.c02e.jpgpj.HashingAlgorithm;
import org.c02e.jpgpj.Key;

/**
 * A gpg compatible SecretAgent implemented using the jpgpj library.
 */
public class JpgpjSecretAgent extends BaseSecretAgent {
    private Key key;

    public JpgpjSecretAgent(Clconf clconf, String armored) throws IOException, PGPException {
        this(clconf, new Key(armored));
        this.key.setNoPassphrase(true);
    }

    public JpgpjSecretAgent(Clconf clconf, Key key) throws IOException, PGPException {
        super(clconf);
        this.key = key;
    }

    @Override
    public String decrypt(String ciphertext) throws IOException {
        try (ByteArrayInputStream in = new ByteArrayInputStream(
                Base64.getDecoder().decode(ciphertext));
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Decryptor decryptor = new Decryptor(key);
            decryptor.setVerificationRequired(false);
            decryptor.decrypt(in, out);
            return out.toString("UTF-8");
        }
        catch (PGPException e) {
            throw new IOException(e);
        }
    }

    @Override
    public String encrypt(String plaintext) throws IOException {
        try (ByteArrayInputStream in = new ByteArrayInputStream(plaintext.getBytes());
                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Encryptor encryptor = new Encryptor(key);
            encryptor.setSigningAlgorithm(HashingAlgorithm.Unsigned);
            encryptor.encrypt(in, out);
            return Base64.getEncoder().encodeToString(out.toByteArray());
        }
        catch (PGPException e) {
            throw new IOException(e);
        }
    }
}
