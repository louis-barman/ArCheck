package org.archcheck.inspect.app;

import dagger.Module;
import org.archcheck.inspect.model.ProjectDetails;
import org.archcheck.inspect.results.ProjectResults;
import org.archcheck.inspect.scan.ConfigurationFile;
import org.archcheck.inspect.scan.Transverse;

/**
 * Copyright (C) 2015 Louis Barman.
 */
@Module(
        injects = {
                Application.class,
                Transverse.class,
                Director.class,
                ProjectDetails.class,
                ConfigurationFile.class,
                ProjectResults.class
        }
)
public class DaggerAppModule {

/*
    @Provides
    @Singleton
    ConfigurationFile provideConfigurationFile() {
        return new ConfigurationFile();
    }*/

    /*
    @Provides
    @Singleton
    Options provideOptions() {
        return new Options();
    }
    */

}
