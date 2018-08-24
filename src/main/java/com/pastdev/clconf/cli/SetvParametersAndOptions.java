package com.pastdev.clconf.cli;


import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;


public class SetvParametersAndOptions {
    @Option( names = "--encrypt", description = "encrypt the value" )
    boolean encrypt;

    @Parameters( index = "0", description = "yaml path" )
    String path;

    @Parameters( index = "1", description = "value at path" )
    String value;
}
