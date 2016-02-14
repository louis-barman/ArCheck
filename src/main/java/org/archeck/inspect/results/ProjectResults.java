package org.archeck.inspect.results;

import org.archeck.inspect.model.ModuleDetails;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2015 Louis Barman.
 */
@Singleton
public class ProjectResults implements Iterable<ModuleResults> {

    List<ModuleResults> moduleResultsList = new ArrayList<ModuleResults>();

    @Inject
    public ProjectResults() {

    }

    public ModuleResults createModuleResults(ModuleDetails moduleDetails) {
        ModuleResults moduleResults = new ModuleResults(moduleDetails);
        moduleResultsList.add(moduleResults);
        return moduleResults;
    }

    @Override
    public Iterator<ModuleResults> iterator() {
        return moduleResultsList.iterator();
    }

    public void clear() {
        moduleResultsList.clear();
    }

}
