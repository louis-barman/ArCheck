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
        println("");
    }

    private void displayComponents(ModuleResults results) {
        long totalFileSize = results.getTotalFileSize();
        tableHeading(11, "Component");
        tableHeading(10, "Circular");
        tableHeading(10, "Percent");
        tableHeading(10, "Private");
        tableHeading(8, "Public");
        println("Group");

        ResultsList table = results.getCodeGroupSummaryTable();
         for (ResultsHolder row : table) {
            displayCodeGroupSummaryRow(row, totalFileSize);
        }
        println("");

    }

    private void displayCodeGroupSummaryRow(ResultsHolder holder, long totalFileSize) {
        boolean component = holder.getBool("component");
        boolean circular = holder.getBool("circular");
        tableRow(11,  yesNo(component));
        tableRow(10, yesNo(circular));
        double percent = (holder.getLong("fileSize")/(double)totalFileSize) * 100.0;
        tableRow(10, String.format("%05.2f%%", percent));
        tableRow(10, "" + holder.getInt("publicClassesSize"));
        tableRow(8, "" + holder.getInt("internalClassesSize"));
        println("" + holder.getString("fullName"));
    }

    private void tableRow(int width, String s) {
        StringBuilder output = new StringBuilder(s);
        while (output.length() < width) {
            output.append(' ');
        }

        print(output.toString() );
    }
    private void tableHeading(int width, String s) {
        tableRow(width, s);
    }

    private String yesNo(boolean state) {
        return (state)?"yes   ":"no    ";
    }

}
