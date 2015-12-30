package org.archeck.inspect.app;

import dagger.ObjectGraph;

import javax.inject.Inject;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class Application {

    @Inject
    Director director;

    public static void main(String[] args) {
        ObjectGraph og = ObjectGraph.create(new DaggerAppModule());
        Application application = og.get(Application.class);

        boolean success = application.run(args);

        if (!success) {
            // This the only way I can find to get main to return a non success error code
            System.exit(1);
        }
    }

    public boolean run(String[] args) {
        return director.start(args);
    }
}
