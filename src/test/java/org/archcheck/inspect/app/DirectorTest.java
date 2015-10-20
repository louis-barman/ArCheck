package org.archcheck.inspect.app;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import org.archcheck.inspect.TestBase;
import org.archcheck.inspect.scan.ConfigurationFile;
import org.archcheck.inspect.util.Outcome;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class DirectorTest extends TestBase {

    // injections
    @Inject
    Director classUnderTest;

    @Inject
    ConfigurationFile configFile;

    // test module override
    @Module(
            includes = DaggerAppModule.class,
            overrides = true,
            injects = {DirectorTest.class}
    )
    static class TestModule {

        @Singleton
        @Provides
        public ConfigurationFile provideConfigurationFile() {
            return Mockito.mock(ConfigurationFile.class);
        }

    }

    // tests
    @Before
    public void setUp() throws Exception {
        ObjectGraph.create(new TestModule()).inject(this);
        Mockito.when(configFile.setConfigFile(Mockito.anyString())).thenReturn(Outcome.success());
    }

    @Test
    public void runConfigTestLong() {
        boolean result = classUnderTest.start(args("--config", "FILE"));
        verify(configFile).setConfigFile("FILE");
        assertEquals(true, result);

    }

    @Test
    public void runConfigTestShort() {
        boolean result = classUnderTest.start(args("-c", "FILE"));
        verify(configFile).setConfigFile("FILE");
        assertEquals(true, result);

    }

    @Test
    public void runConfigTest_withNoArguments() {
        boolean result = classUnderTest.start(args("-config"));
        verify(configFile, never()).setConfigFile(anyString());
        assertEquals(false, result);
    }

    @Test
    public void runConfigTest_withNoEquals() {
        boolean result = classUnderTest.start(args("-config"));
        verify(configFile, never()).setConfigFile(anyString());
        assertEquals(false, result);
    }

    private String[] args(String... args) {
        return args;
    }

}
