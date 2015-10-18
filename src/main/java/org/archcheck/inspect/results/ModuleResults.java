package org.archcheck.inspect.results;

import org.archcheck.inspect.model.ModuleDetails;
import org.archcheck.inspect.model.ResultsHolder;
import org.archcheck.inspect.model.ResultsList;
import org.archcheck.inspect.options.Options;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class ModuleResults {
    private final ModuleDetails moduleDetails;


    public ModuleResults(ModuleDetails moduleDetails) {


        this.moduleDetails = moduleDetails;
    }


    public String getModuleName() {
        return moduleDetails.getModuleName();
    }


    public ResultsList getCodeGroupSummaryTable() {
        return moduleDetails.getCodeGroupSummaryTable();
    }

    public ResultsList getPackageInfo() {
        return moduleDetails.getPackageInfo();
    }

    public Options getOptions() {
        return moduleDetails.getOptions();
    }

    public long getTotalFileSize() {
        return moduleDetails.getTotalFileSize();
    }
}
