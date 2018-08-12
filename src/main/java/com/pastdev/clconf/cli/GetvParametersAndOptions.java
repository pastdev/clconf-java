package com.pastdev.clconf.cli;

import lombok.Getter;
import lombok.NoArgsConstructor;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@NoArgsConstructor(force = true)
public class GetvParametersAndOptions {
    @Getter
    @Parameters(index = "0", arity = "0..1", description = "yaml path")
    private String path = "";

    @Getter
    @Option(names = "--decrypt", description = "A list of paths whose values needs to be decrypted")
    private String decript;

    @Getter
    @Option(names = "--default",
            description = ""
                    + "The value to be returned if the specified path does not "
                    + "exist (otherwise results in an error).")
    private String defaultValue;

    @Getter
    @Option(names = "--template",
            description = "A ? template file that will be executed against the resulting data.")
    private String template;

    @Getter
    @Option(names = "--template-base64",
            description = ""
                    + "A base64 encoded string containing a ? template that "
                    + "will be executed against the resulting data.")
    private String templateBase64;

    @Getter
    @Option(names = "--template-string",
            description = ""
                    + "A string containing a ? template that will be executed "
                    + "against the resulting data.")
    private String templateString;
}
