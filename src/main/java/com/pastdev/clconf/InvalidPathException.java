package com.pastdev.clconf;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InvalidPathException extends IllegalArgumentException {
    private static final long serialVersionUID = 5576352703885533357L;

    public InvalidPathException(String message, String path) {
        super("invalid path [" + path + "]: " + message);
    }

    public InvalidPathException(String message, String[] parts, String pathSeparator) {
        this(message, Arrays.asList(parts), pathSeparator);
    }

    public InvalidPathException(String message, List<String> parts, String pathSeparator) {
        this(message, parts.stream().collect(Collectors.joining(pathSeparator)));
    }
}