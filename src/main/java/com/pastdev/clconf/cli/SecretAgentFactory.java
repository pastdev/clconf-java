package com.pastdev.clconf.cli;

import java.io.IOException;

import com.pastdev.clconf.SecretAgent;

/**
 * A factory for obtaining a secret agent.  This is needed because the key
 * file/base64 may be specified by either environment or command line option.
 * The late binding of command line options (after the spring container has
 * initialized) necessitates this.
 */
public interface SecretAgentFactory {
    SecretAgent fromGlobalOptions(com.pastdev.clconf.Clconf clconf,
            GlobalOptions globalOptions) throws IOException;
}
