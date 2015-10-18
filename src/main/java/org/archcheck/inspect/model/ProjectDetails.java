package org.archcheck.inspect.model;

import org.archcheck.inspect.options.Options;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Copyright (C) 2015 Louis Barman.
 */
@Singleton
public class ProjectDetails {

    @Inject
    public ProjectDetails() {

    }

    private List<ModuleDetails> moduleList = new ArrayList<ModuleDetails>();

    public List<ModuleDetails> getModuleList() {
        return Collections.unmodifiableList(moduleList);
    }

    public ModuleDetails createNewModuleItem() {
        Options moduleOptions = new Options();
        ModuleDetails moduleDetails = new ModuleDetails(moduleOptions);
        moduleList.add(moduleDetails);
        return moduleDetails;
    }

    public void analysePhase() {
        for (ModuleDetails module : moduleList) {
            module.analysePhase();
        }
    }

}
