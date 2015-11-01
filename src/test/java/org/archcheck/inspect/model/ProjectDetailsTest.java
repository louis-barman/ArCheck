package org.archcheck.inspect.model;

import dagger.Module;
import dagger.ObjectGraph;
import org.archcheck.inspect.TestBase;
import org.archcheck.inspect.app.DaggerAppModule;
import org.archcheck.inspect.scan.ConfigurationFile;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class ProjectDetailsTest extends TestBase {

    @Inject
    ProjectDetails classUnderTest;

    @Inject
    ConfigurationFile configFile;

    // test module override
    @Module(
            includes = DaggerAppModule.class,
            overrides = true,
            injects = {ProjectDetailsTest.class, ProjectDetails.class}

    )
    class TestModule {

    }

    private String getTestConfigFileName() {
        return super.getTestConfigFileName("test-config.config");
    }

    @Before
    public void setUp() throws Exception {
        ObjectGraph.create(new TestModule()).inject(this);
    }

    @Test
    public void configFile_hasModuleNames() {
        String testFileName = getTestConfigFileName();

        configFile.setConfigFile(testFileName);

        List<ModuleDetails> moduleList = classUnderTest.getModuleList();
        assertNotNull(moduleList);

        assertEquals(2, moduleList.size());
        assertEquals("AAA", moduleList.get(0).getModuleName());
        assertEquals("BBB", moduleList.get(1).getModuleName());
        List<String> dirs = (List<String>) moduleList.get(0).getSourceDirs();
        assertEquals(1, dirs.size());
        assertEquals("path/one", dirs.get(0));
        dirs = (List<String>) moduleList.get(1).getSourceDirs();
        assertEquals(2, dirs.size());
        assertEquals("path/two-A", dirs.get(0));
        assertEquals("path/two-B", dirs.get(1));
    }

}

