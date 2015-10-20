package org.archcheck.inspect.scan;

import dagger.Module;
import dagger.ObjectGraph;
import org.archcheck.inspect.TestBase;
import org.archcheck.inspect.app.DaggerAppModule;
import org.archcheck.inspect.model.ProjectDetails;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;


public class TransverseTest extends TestBase {

    // injections
    @Inject
    Transverse classUnderTest;

    @Inject
    ConfigurationFile configFile;

    // test module override
    @Module(
            includes = DaggerAppModule.class,
            overrides = true,
            injects = {TransverseTest.class, Transverse.class, ProjectDetails.class}

    )
    class TestModule {


    }

    // tests
    @Before
    public void setUp() throws Exception {
        ObjectGraph.create(new TestModule()).inject(this);
    }


    @Test
    public void runTest() {
        String testFileName = getTestConfigFileName("archcheck.config");

        configFile.setConfigFile(testFileName);

        classUnderTest.start();
    }

}
