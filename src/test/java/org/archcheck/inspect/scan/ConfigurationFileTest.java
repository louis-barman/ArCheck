package org.archcheck.inspect.scan;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import org.archcheck.inspect.TestBase;
import org.archcheck.inspect.app.DaggerAppModule;
import org.archcheck.inspect.options.Options;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.inject.Inject;
import javax.inject.Singleton;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;


public class ConfigurationFileTest extends TestBase {

    @Inject
    Options options;

    @Inject
    ConfigurationFile classUnderTest;

    // test module override
    @Module(
            includes = DaggerAppModule.class,
            overrides = true,
            injects = {ConfigurationFileTest.class}
    )
    static class TestModule {

        @Singleton
        @Provides
        public Options provideOptions() {
            return Mockito.mock(Options.class);
        }

    }


    @Before
    public void setUp() throws Exception {
        ObjectGraph.create(new TestModule()).inject(this);
    }

    @Test
    public void configFile_hasRootDirectory() {
        String testFileName = getTestConfigFileName();

        classUnderTest.setConfigFile(testFileName);

        assertEquals("example root dir", classUnderTest.getProjectRootDir());

    }

    private String getTestConfigFileName() {
        return super.getTestConfigFileName("test-config.config");
    }

    void setTestConfigFileName(String testConfig) {
        String testFileName = getTestConfigFileName(testConfig);
        classUnderTest.setConfigFile(testFileName);
    }

    @Test
    public void test_thereAreNoHiddenImports() {
        setTestConfigFileName("test-config.config");
        verify(options, never()).addHiddenImport(anyString());

    }

    @Test
    public void test_thereAreTwoHiddenImports() {
        setTestConfigFileName("test-config-withHiddenImports.config");
        verify(options, times(2)).addHiddenImport(anyString());
        verify(options).addHiddenImport("hide.package1.Class1");
        verify(options).addHiddenImport("hide.package2.Class2");
    }


}
