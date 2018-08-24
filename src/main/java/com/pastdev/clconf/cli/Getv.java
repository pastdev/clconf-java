package com.pastdev.clconf.cli;


import static com.pastdev.clconf.impl.MapUtil.castList;
import static com.pastdev.clconf.impl.MapUtil.castMap;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;


import com.pastdev.clconf.InvalidPathException;
import com.pastdev.clconf.SecretAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;


@Slf4j
@Command( name = "getv", description = "Get a value" )
public class Getv implements Callable<Void> {
    @Autowired
    private com.pastdev.clconf.Clconf clconf;
    @Mixin
    private GlobalOptions globalOptions;
    @Mixin
    private GetvParametersAndOptions parametersAndOptions;
    @Autowired
    private SecretAgentFactory secretAgentFactory;

    Object getValue() throws IOException {
        Map<String, Object> configuration = clconf.loadConfigurationFromEnvironment(
                globalOptions.getYaml(),
                globalOptions.getYamlBase64() );
        if ( parametersAndOptions.getPath() == null || parametersAndOptions.getPath().isEmpty() ) {
            log.debug( "getValue empty path" );
            return configuration;
        }

        log.debug( "getValue for path {}", parametersAndOptions.getPath() );
        Object value;
        try {
            value = clconf.getValue( configuration, parametersAndOptions.getPath() );
        }
        catch ( InvalidPathException e ) {
            if ( parametersAndOptions.getDefaultValue() == null ) {
                throw e;
            }
            value = parametersAndOptions.getDefaultValue();
        }

        if ( parametersAndOptions.getDecrypt().length > 0 ) {
            SecretAgent secretAgent = secretAgentFactory.fromGlobalOptions( clconf, globalOptions );
            if ( value instanceof Map ) {
                secretAgent.decryptPaths(
                        castMap( value ),
                        parametersAndOptions.getDecrypt() );
            }
            else if ( value instanceof List ) {
                List<Object> list = castList( value );
                for ( int i = 0; i < list.size(); i++ ) {
                    list.set( i, secretAgent.decrypt( list.get( i ).toString() ) );
                }
            }
            else {
                value = secretAgent.decrypt( value.toString() );
            }
        }

        return value;
    }

    @Override
    public Void call() throws Exception {
        System.out.println( clconf.marshalYaml( getValue() ) );
        return null;
    }
}
