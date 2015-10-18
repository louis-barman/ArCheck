package org.archcheck.inspect.report;

import org.archcheck.inspect.model.ResultsHolder;
import org.archcheck.inspect.model.ResultsList;
import org.archcheck.inspect.output.OutputWrapper;
import org.archcheck.inspect.results.ModuleResults;
import org.archcheck.inspect.results.ProjectResults;

import java.util.List;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class TextGenerator extends ReportGenerator {


    public TextGenerator(OutputWrapper output) {
        super(output);
    }

    @Override
    public boolean generateReports(ProjectResults projectResults) {
        for (ModuleResults moduleResults : projectResults) {
            generateOutput(moduleResults);
        }
        return true;
    }

    private void generateOutput(ModuleResults results) {
        displayHeading(results);

        displayComponents(results);

        println("");
    }

    private void displayHeading(ModuleResults results) {
        println("");
        println("========================================== ");
        println("MODULE: " + results.getModuleName());
        println("========================================== ");
    }

    private void displayComponents(ModuleResults results) {
        println("");
        println("Components:");
        println("----------- ");

        ResultsList table = results.getCodeGroupSummaryTable();
        for (ResultsHolder row : table) {
            displayComponentsRow(row);
        }
    }

    private void displayComponentsRow(ResultsHolder holder) {
        println(holder.getString("elementKey"));
        ResultsList row = holder.getResultsList("visiblePackagesList");

        for (int i = 0; i < row.size(); i++) {
            println("     -> " + row.getString(i));
        }
    }

}
