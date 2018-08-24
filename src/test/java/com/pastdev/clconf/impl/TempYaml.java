package com.pastdev.clconf.impl;


import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;


public class TempYaml implements Closeable {
    private Path path;

    public TempYaml( String content ) throws IOException {
        path = Files.createTempFile( UUID.randomUUID().toString(), ".yml" );
        Files.write( path, content.getBytes() );
    }

    @Override
    public void close() throws IOException {
        if ( Files.exists( path ) ) {
            Files.delete( path );
        }
    }

    @Override
    public String toString() {
        return path.toString();
    }
}
