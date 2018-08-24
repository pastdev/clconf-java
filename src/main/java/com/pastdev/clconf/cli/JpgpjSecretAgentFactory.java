package com.pastdev.clconf.cli;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;


import com.pastdev.clconf.Clconf;
import com.pastdev.clconf.impl.JpgpjSecretAgent;
import org.bouncycastle.openpgp.PGPException;


public class JpgpjSecretAgentFactory implements SecretAgentFactory {
    private JpgpjSecretAgent fromArmored( Clconf clconf, String armored ) throws IOException {
        try {
            return new JpgpjSecretAgent( clconf, armored );
        }
        catch ( PGPException e ) {
            throw new IOException( e );
        }
    }

    private JpgpjSecretAgent fromBase64( Clconf clconf, String base64 ) throws IOException {
        return fromArmored( clconf, new String( Base64.getDecoder().decode( base64 ) ) );
    }

    private JpgpjSecretAgent fromFile( Clconf clconf, String path ) throws IOException {
        return fromArmored( clconf, new String( Files.readAllBytes( Paths.get( path ) ) ) );
    }

    @Override
    public JpgpjSecretAgent fromGlobalOptions( Clconf clconf, GlobalOptions globalOptions )
            throws IOException {
        if ( globalOptions.getSecretKeyringBase64() != null ) {
            return fromBase64( clconf, globalOptions.getSecretKeyringBase64() );
        }
        else if ( globalOptions.getSecretyKeyring() != null ) {
            return fromFile( clconf, globalOptions.getSecretyKeyring() );
        }
        else if ( System.getenv( Clconf.ENV_SECRET_KEYRING_BASE64 ) != null ) {
            return fromBase64( clconf, System.getenv( Clconf.ENV_SECRET_KEYRING_BASE64 ) );
        }
        else if ( System.getenv( Clconf.ENV_SECRET_KEYRING ) != null ) {
            return fromFile( clconf, System.getenv( Clconf.ENV_SECRET_KEYRING ) );
        }
        else {
            throw new IllegalArgumentException( ""
                    + "requires --secret-keyring-base64, --secret-keyring, "
                    + "SECRET_KEYRING_BASE64, or SECRET_KEYRING" );
        }
    }
}
