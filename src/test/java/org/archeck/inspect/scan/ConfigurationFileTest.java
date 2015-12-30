package org.archeck.inspect.scan;

import dagger.Module;
import dagger.ObjectGraph;
import org.archeck.inspect.TestBase;
import org.archeck.inspect.app.DaggerAppModule;
import org.archeck.inspect.model.ProjectDetails;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;


public class ConfigurationFileTest extends TestBase {

    @Inject
    ProjectDetails projectDetails;

    @Inject
    ConfigurationFile classUnderTest;

    // test module override
    @Module(
            includes = DaggerAppModule.class,
            overrides = true,
            injects = {ConfigurationFileTest.class}
    )
    static class TestModule {


    }


    @Before
    public void setUp() throws Exception {
        ObjectGraph.create(new TestModule()).inject(this);
    }

    @Test
    public void configFile_hasRootDirectory() {
        String testFileName = getTestConfigFileName();

        classUnderTest.setConfigFile(testFileName);
        String rootDir = classUnderTest.getProjectRootDir();
        assertNotNull(rootDir);
        assertTrue(rootDir.endsWith("./"));
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
        assertEquals(2, projectDetails.getModuleList().size());
        assertEquals(0, projectDetails.getModuleList().get(0).getOptions().getHiddenImports().size());
        assertEquals(0, projectDetails.getModuleList().get(1).getOptions().getHiddenImports().size());
    }

    @Test
    public void test_thereAreTwoHiddenImports() {
        setTestConfigFileName("test-config-withHiddenImports.config");
        assertEquals(2, projectDetails.getModuleList().size());
        assertEquals(0, projectDetails.getModuleList().get(0).getOptions().getHiddenImports().size());
        assertEquals(2, projectDetails.getModuleList().get(1).getOptions().getHiddenImports().size());
        assertEquals("hide.package1.Class1", projectDetails.getModuleList().get(1).getOptions().getHiddenImports().get(0));
        assertEquals("hide.package2.Class2", projectDetails.getModuleList().get(1).getOptions().getHiddenImports().get(1));
    }

    @Test
    public void test_badlyFormattedConfigFile() {
        String testFileName = getTestConfigFileName("bad-config.config");
        classUnderTest.setConfigFile(testFileName);
    }

}
