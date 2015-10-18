package org.archcheck.inspect.app;

import org.archcheck.inspect.TestBase;
import org.junit.Test;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class MainTest extends TestBase {
    private String[] createArgs(String... argsList) {
        String[] args = new String[argsList.length];
        for (int i = 0; i < argsList.length; i++) {
            args[i] = argsList[i];
        }
        return args;
    }

    @Test
    public void startMain() {

        String testFileName = "src/test/resources/archcheck.config";


        Application.main(createArgs("--config", testFileName));

    }

    @Test
    public void startHtmlOutput() {
        String testFileName = "src/test/resources/archcheck.config";

        Application.main(createArgs("--config", testFileName, "--html", "build/reports/archcheck"));

    }


}