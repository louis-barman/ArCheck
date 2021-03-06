package org.archeck.inspect.report;

import org.archeck.inspect.model.ResultsHolder;
import org.archeck.inspect.model.ResultsList;
import org.archeck.inspect.options.Options;
import org.archeck.inspect.output.OutputWrapper;
import org.archeck.inspect.results.ModuleResults;
import org.archeck.inspect.results.ProjectResults;
import org.archeck.inspect.util.XLog;

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

    protected void resetData() {
        bothWaysArrowList.clear();
        shortNameLookUp.clear();
        uniqueShortNameList.clear();
        hideComponents.clear();
    }

    protected boolean generateOutput(ModuleResults results) {
        String name = results.getModuleName();
        openFile(name + "/" + name + EXTN_DOT);
        println("digraph \"" + name + "\"  {");

        println("graph [" + getDrawOptions(name) + "];");
        println("node [style = filled ];");

        displayComponents(results);
        println("}");
        closeFile();
        return startGraphViz(name);
    }

    protected String getDrawOptions(String name) {
        String label = "Dependencies for: " + name;

        if (hideComponents.size() > 0) {
            label += " (ignoring " + hideComponents.size() + " items)";
        }
        return "dpi = 65, label=\"" + label + "\"";
    }

    protected void displayComponents(ModuleResults results) {
        ResultsList table = results.getPackageInfo();

        for (ResultsHolder row : table) {
            generateGraph(row);
        }
    }

    protected void generateGraph(ResultsHolder holder) {
        String shortName = getUniqueShortName(holder.getString("name"));
        if (shortName.isEmpty()) {
            return;
        }
        ResultsList imports = holder.getResultsList("imports");
        ResultsList crossRef = holder.getResultsList("crossRef");

        if (imports.size() == 0 && crossRef.size() == 0 && Options.IGNORE_NODES_WITH_NO_CONNECTIONS) {
            return;
        }

        String stateColour = getStateColour(holder.getBool("circular"));

        println("\"" + shortName + "\" [color=\"" + stateColour + "\"];");


        for (ResultsHolder row : imports) {
            generateOneRow(shortName, row);
        }
    }

    protected String getStateColour(boolean circular) {
        if (circular) {
            return "#E8CFDE"; //bad red
        } else {
            return "#cbd5e8"; //good blue
        }
    }


    protected void generateOneRow(String rootName, ResultsHolder row) {
        String targetName = getUniqueShortName(row.getString("name"));
        if (targetName.isEmpty()) {
            return;
        }
        if (onBothWaysList(rootName, targetName)) {
            return;
        }
        print("  \"" + rootName + "\" -> \"" + targetName + "\"");

        if (row.getBool("circularLoop")) {
            print(" [dir=both color=red]");
            addToBothWaysList(rootName, targetName);
        } else if (row.getBool("circularWeek")) {
            print(" [color=\"#BA8AA4\"]"); // week red
        } else {

            print(" [color=\"#8081B0\"]"); // grey
        }
        println(";");
    }


    protected void addToBothWaysList(String rootName, String importName) {
        bothWaysArrowList.add(rootName + '+' + importName);
    }

    protected boolean onBothWaysList(String rootName, String importName) {
        return bothWaysArrowList.contains(importName + '+' + rootName);
    }

    protected boolean ignoreName(String name) {
        for (String ignore : hideComponents) {
            if (ignore.equals(name)) {
                return true;
            }
        }
        return false;
    }

    protected String getUniqueShortName(String longName) {
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

    protected String createUniqueShortName(String longName) {
        String shortName = createShortName(longName, 0);

        for (int i = 1; i < 10; i++) {
            if (!uniqueShortNameList.contains(shortName)) {
                uniqueShortNameList.add(shortName);
                break;
            }
            shortName = createShortName(longName, i);
        }
        return shortName;
    }

    protected String createShortName(String longName, int dotIndex) {
        int dotCounter = 0;
        int n = longName.length() - 1;

        while (n > 0) {
            if (longName.charAt(n) == '.') {
                if (dotCounter == dotIndex) {
                    return longName.substring(n + 1);
                }
                dotCounter++;
            }
            n--;
        }

        return longName;
    }

    protected boolean startGraphViz(String name) {
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
