package com.pastdev.clconf.cli;


import java.util.concurrent.Callable;


import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;


@Slf4j
@Command( mixinStandardHelpOptions = true,
        subcommands = {
                Getv.class,
                Setv.class,
        },
        description = ""
                + "clconf, a utility for merging multiple config files and "
                + "extracting values using a path string" )
public class Clconf implements Callable<Void> {
    @Mixin
    private GlobalOptions globalOptions;

    @Override
    public Void call() throws Exception {
        log.trace( "clconf\n----- RESULT -----\n{}\n--- END RESULT ---", "NOTHING" );
        System.out.println( "YOU RULE!" );
        return null;
    }
}
