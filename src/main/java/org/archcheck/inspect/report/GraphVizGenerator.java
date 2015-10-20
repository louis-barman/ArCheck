package org.archcheck.inspect.report;

import org.archcheck.inspect.model.ResultsHolder;
import org.archcheck.inspect.model.ResultsList;
import org.archcheck.inspect.options.Options;
import org.archcheck.inspect.output.OutputWrapper;
import org.archcheck.inspect.results.ModuleResults;
import org.archcheck.inspect.results.ProjectResults;

import java.io.IOException;
import java.util.*;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class GraphVizGenerator extends ReportGenerator {
    public static final String EXTN_DOT = ".dot";

    private final Set<String> bothWaysArrowList = new HashSet<String>();
    private final Map<String, String> shortNameLookUp = new HashMap<String, String>();
    private final Set<String> uniqueShortNameList = new HashSet<String>();
    private List<String> hideStuff;

    public GraphVizGenerator(OutputWrapper output) {
        super(output);
    }

    @Override
    public boolean generateReports(ProjectResults projectResults) {
        for (ModuleResults moduleResults : projectResults) {
            resetData();
            Options options = moduleResults.getOptions();
            hideStuff(options.getHiddenImports());
            generateOutput(moduleResults);
        }
        return true;
    }

    private void resetData() {
        bothWaysArrowList.clear();
        shortNameLookUp.clear();
        uniqueShortNameList.clear();
    }

    private void generateOutput(ModuleResults results) {
        String name = results.getModuleName();
        openFile(name + "/" + name + EXTN_DOT);
        println("digraph \"" + name + "\"  {");
        println("graph [dpi = 65, label=\" Dependencies for: " + name + "\"];");
        println("node [style = filled ];");

        displayComponents(results);
        println("}");
        closeFile();
        startGraphViz(name);
    }

    private void displayComponents(ModuleResults results) {
        ResultsList table = results.getPackageInfo();

        for (ResultsHolder row : table) {
            generateGraph(row);
        }
    }

    private void generateGraph(ResultsHolder holder) {
        String rootName = getUniqueShortName(holder.getString("name"));
        if (ignoreName(rootName)) {
            return;
        }
        ResultsList imports = holder.getResultsList("imports");
        ResultsList crossRef = holder.getResultsList("crossRef");;
        if (imports.size() == 0 && crossRef.size() == 0 ) {
            return;
        }

        String stateColour = getStateColour(holder.getBool("component"), holder.getBool("circular"));

        println("\"" + rootName + "\" [color=\"" + stateColour + "\"];");


        for (ResultsHolder row : imports) {
            generateOneRow(rootName, row);
        }
    }

    private String getStateColour(boolean component, boolean circular) {
        if (component) {
            return "#cbd5e8"; //good blue
        } else if (circular) {
            return "#E8CFDE"; //bad red
        } else {
            return "#F4E395"; // warn
        }
    }


    private void generateOneRow(String rootName, ResultsHolder row) {
        String importName = getUniqueShortName(row.getString("name"));
        if (ignoreName(importName)) {
            return;
        }
        if (onBothWaysList(rootName, importName)) {
            return;
        }
        print("  \"" + rootName + "\" -> \"" + importName + "\"");

        if (row.getBool("circularLoop")) {
            print(" [dir=both color=red]");
            addToBothWaysList(rootName, importName);
        } else if (row.getBool("circularWeek")) {
            print(" [color=\"#BA8AA4\"]"); // week red
        } else {

            print(" [color=\"#8081B0\"]"); // grey
        }
        println(";");
    }


    private void addToBothWaysList(String rootName, String importName) {
        bothWaysArrowList.add(rootName + '+' + importName);
    }

    private boolean onBothWaysList(String rootName, String importName) {
        return bothWaysArrowList.contains(importName + '+' + rootName);
    }

    private boolean ignoreName(String name) {
        if (hideStuff == null) {
            return false;
        }
        for (String ignore : hideStuff) {
            if (ignore.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private String getUniqueShortName(String longName) {
        String shortName = shortNameLookUp.get(longName);
        if (shortName == null) {
            shortName = createUniqueShortName(longName);
            shortNameLookUp.put(longName, shortName);
        }
        return shortName;
    }

    private String createUniqueShortName(String longName) {
        String shortName = createShortName(longName);
        for (int i = 0; i < 100; i++) {
            if (!uniqueShortNameList.contains(shortName)) {
                uniqueShortNameList.add(shortName);
                break;
            } else {
                shortName += "-V2";
            }
        }
        return shortName;
    }

    private String createShortName(String longName) {

        int index = longName.lastIndexOf('.');
        if (index < 0) {
            return longName;
        }
        return longName.substring(index + 1);


    }

    private void startGraphViz(String name) {
        String path = output.getAbsolutePath() + "/" + name + "/" + name;
        ProcessBuilder builder = new ProcessBuilder("dot", "-Tsvg", "-o" + path + ".svg", path + EXTN_DOT);
        try {
            Process process = builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void hideStuff(List<String> hideStuff) {

        this.hideStuff = hideStuff;
    }
}
