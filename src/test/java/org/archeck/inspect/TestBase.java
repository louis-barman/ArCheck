package org.archeck.inspect;

import java.net.URL;

import static junit.framework.TestCase.assertNotNull;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public abstract class TestBase {
    protected String getTestConfigFileName(String propertiesFileName) {
        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();

        URL fileUrl = classLoader.getResource(propertiesFileName);
        assertNotNull("propertiesFileName does not exists: " + propertiesFileName, fileUrl);
        return fileUrl.getFile();
    }

}
