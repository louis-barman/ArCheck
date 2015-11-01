package org.archcheck.inspect.report;

import org.archcheck.inspect.model.ResultsHolder;
import org.archcheck.inspect.model.ResultsList;
import org.archcheck.inspect.options.Options;
import org.archcheck.inspect.output.OutputWrapper;
import org.archcheck.inspect.results.ModuleResults;
import org.archcheck.inspect.results.ProjectResults;
import org.archcheck.inspect.util.XLog;

import java.io.IOException;
import java.util.*;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class GraphVizGenerator extends ReportGenerator {
    public static final String EXTN_DOT = ".dot";

    private final ReportLimits reportLimits;

    private final Set<String> bothWaysArrowList = new HashSet<String>();
    private final Map<String, String> shortNameLookUp = new HashMap<String, String>();
    private final Set<String> uniqueShortNameList = new HashSet<String>();
    private final List<String> hideComponents = new ArrayList<String>();

    public GraphVizGenerator(OutputWrapper output) {
        super(output);
        reportLimits = new ReportLimits(); // TODO MOVE
    }

    @Override
    public boolean generateReports(ProjectResults projectResults) {

        for (ModuleResults moduleResults : projectResults) {
            resetData();
            Options options = moduleResults.getOptions();
            hideComponents(reportLimits.getComponentTrimList(moduleResults));
            hideComponents(options.getHiddenImports());
            boolean success = generateOutput(moduleResults);
            if (!success) {
                return false;
            }
        }
        return true;
    }

    private void resetData() {
        bothWaysArrowList.clear();
        shortNameLookUp.clear();
        uniqueShortNameList.clear();
        hideComponents.clear();
    }

    private boolean generateOutput(ModuleResults results) {
        String name = results.getModuleName();
        openFile(name + "/" + name + EXTN_DOT);
        println("digraph \"" + name + "\"  {");

        String label = "Dependencies for: " + name;

        if (hideComponents.size() > 0) {
            label += " (ignoring " + hideComponents.size() +" items)";
        }

        println("graph [dpi = 65, label=\"" + label + "\"];");
        println("node [style = filled ];");

        displayComponents(results);
        println("}");
        closeFile();
        return startGraphViz(name);
    }

    private void displayComponents(ModuleResults results) {
        ResultsList table = results.getPackageInfo();

        for (ResultsHolder row : table) {
            generateGraph(row);
        }
    }

    private void generateGraph(ResultsHolder holder) {
        String shortName = getUniqueShortName(holder.getString("name"));
        if (shortName.isEmpty()) {
            return;
        }
        ResultsList imports = holder.getResultsList("imports");
        ResultsList crossRef = holder.getResultsList("crossRef");
        ;
        if (imports.size() == 0 && crossRef.size() == 0) {
            return;
        }

        String stateColour = getStateColour(holder.getBool("component"), holder.getBool("circular"));

        println("\"" + shortName + "\" [color=\"" + stateColour + "\"];");


        for (ResultsHolder row : imports) {
            generateOneRow(shortName, row);
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
        String shortName = getUniqueShortName(row.getString("name"));
        if (shortName.isEmpty()) {
            return;
        }
        if (onBothWaysList(rootName, shortName)) {
            return;
        }
        print("  \"" + rootName + "\" -> \"" + shortName + "\"");

        if (row.getBool("circularLoop")) {
            print(" [dir=both color=red]");
            addToBothWaysList(rootName, shortName);
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
         for (String ignore : hideComponents) {
            if (ignore.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private String getUniqueShortName(String longName) {
        if (ignoreName(longName)) {
            return "";//"*other*";
        }
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

    private boolean startGraphViz(String name) {
        String path = output.getAbsolutePath() + "/" + name + "/" + name;
        ProcessBuilder builder = new ProcessBuilder("dot", "-Tsvg", "-o" + path + ".svg", path + EXTN_DOT);
        try {
            Process process = builder.start();
            int errorCode = process.waitFor();
            if (errorCode == 0) {
                return true;
            } else {
                XLog.error("Graphviz - Exited with an error");
                return false;

            }
        } catch (IOException e) {
            XLog.error("Graphviz - Graph Visualization Software not found in PATH");
            return true;
        } catch (InterruptedException e) {
            XLog.e("Graphviz " + e.getMessage());
            return false;
        }
    }

    public void hideComponents(List<String> fullNames) {

        this.hideComponents.addAll(fullNames);
    }
}
