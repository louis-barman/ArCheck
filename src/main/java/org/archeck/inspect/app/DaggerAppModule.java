package org.archeck.inspect.app;

import dagger.Module;
import org.archeck.inspect.model.ProjectDetails;
import org.archeck.inspect.results.ProjectResults;
import org.archeck.inspect.scan.ConfigurationFile;
import org.archeck.inspect.scan.Transverse;

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
