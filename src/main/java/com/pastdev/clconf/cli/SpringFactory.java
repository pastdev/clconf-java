package com.pastdev.clconf.cli;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import picocli.CommandLine.IFactory;


/**
 * Provides Spring DI integration with picocli.
 *
 * @see <a href="https://picocli.info/#_dependency_injection">Dependency Injection</a>
 */
public class SpringFactory implements IFactory {
    @Autowired
    private ApplicationContext context;

    @Override
    public <K> K create( Class<K> cls ) throws Exception {
        return context.getBean( cls );
    }
}
