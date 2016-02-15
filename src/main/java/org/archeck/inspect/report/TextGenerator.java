package org.archeck.inspect.report;

import org.archeck.inspect.model.ResultsHolder;
import org.archeck.inspect.model.ResultsList;
import org.archeck.inspect.output.OutputWrapper;
import org.archeck.inspect.results.ModuleResults;
import org.archeck.inspect.results.ProjectResults;

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
        tableHeading(10, "Circular");
        tableHeading(10, "Percent");
        tableHeading(10, "Private");
        tableHeading(8, "Public");
        println("Package");

        ResultsList table = results.getCodeGroupSummaryTable();
         for (ResultsHolder row : table) {
            displayCodeGroupSummaryRow(row, totalFileSize);
        }
        println("");

    }

    private void displayCodeGroupSummaryRow(ResultsHolder holder, long totalFileSize) {

        boolean circular = holder.getBool("circular");
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
