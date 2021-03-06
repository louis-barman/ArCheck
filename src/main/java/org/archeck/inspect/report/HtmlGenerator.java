package org.archeck.inspect.report;

import org.archeck.inspect.model.ResultsHolder;
import org.archeck.inspect.model.ResultsList;
import org.archeck.inspect.output.OutputWrapper;
import org.archeck.inspect.results.ModuleResults;
import org.archeck.inspect.results.ProjectResults;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class HtmlGenerator extends HtmlGeneratorUtils {
    private static final boolean CONFIG_ENABLE_CLASS_OUTPUT = true;

    private static final String DIR_CSS = "css/";
    private static final String DIR_JS = "js/";
    private static final String DIR_CLASSES = "classes/";
    private static final String DIR_PACKAGES = "packages/";
    private static final String INDEX = "index";
    private String outputModuleName;

    public HtmlGenerator(OutputWrapper output) {
        super(output);
        setResourceFilePath("html/html-snippets/");
        setResourceFileExtension(".html");
    }


    private String classLink(String link, String text) {
        return link("../" + DIR_CLASSES + link, text);
    }

    private String classLink(String link) {
        return classLink(link, link);
    }

    private String packageLink(String link) {
        return link("../" + DIR_PACKAGES + link, link);
    }

    private String packageLinkRoot(String link) {
        return link(DIR_PACKAGES + link, link);
    }

    private void divColour(ResultsHolder item, String boolTag, String trueStyle, String falseStyle) {
        divStart(item.getBool(boolTag) ? trueStyle : falseStyle);
    }

    private void divComponent(ResultsHolder item) {
        divStart(getComponentStyle(item));
    }

    private String getComponentStyle(ResultsHolder item) {
        return item.getBool("circular") ? "bad" : "good";
    }

    private void fontComponentColour(ResultsHolder item, String text) {
        fontColour(getComponentStyle(item), text);
    }


    private void displayBreadCrumb(int depth, String... links) {
        final String projectName = "Project";
        if (depth == 0) {
            print(projectName);
        } else if (depth == 1) {
            print(link("../index", projectName));
        } else if (depth == 2) {
            print(link("../../index", projectName));
        }
        if (outputModuleName == null) {
            return;
        }
        for (String link : links) {
            print(" -> " + link);
        }
    }

    private String getModuleLink() {
        return link("../" + INDEX, outputModuleName);
    }


    private void createStyleSheet() {
        openFile(DIR_CSS + "style.css");
        print(getResourceString("/css/style.css"));
        closeFile();
    }

    private void createJavaScripts() {
        openFile(DIR_JS + "jquery-latest.js");
        print(getResourceString("/js/jquery-latest.js"));
        closeFile();
        openFile(DIR_JS + "jquery.tablesorter.js");
        print(getResourceString("/js/jquery.tablesorter.min.js"));
        closeFile();
    }


    @Override
    public boolean generateReports(ProjectResults projectResults) {

        createStyleSheet();
        createJavaScripts();

        for (ModuleResults moduleResults : projectResults) {

            if (aborted()) {
                return false;
            }

            setModuleOutputName(moduleResults);

            generateModuleDetails(moduleResults);

            generateCodeGroupDetails(moduleResults);

         }

        generateProjectDetails(projectResults);

        return !aborted();
    }

    protected void setModuleOutputName(ModuleResults moduleResults) {
        outputModuleName = moduleResults.getModuleName();
    }

    private void generateProjectDetails(ProjectResults projectResults) {
        if (aborted()) {
            return;
        }
        openHtmlFile(0, "Project", INDEX);
        displayBreadCrumb(0);
        printSnippet(HtmlStr.h1, "Project");
        for (ModuleResults moduleResults : projectResults) {
            String moduleName = moduleResults.getModuleName();
            printBr(link(moduleName + '/' + INDEX, moduleName));
        }
        closeHtml();
    }

    private void generateModuleDetails(ModuleResults results) {

        openHtmlFile(1, outputModuleName, outputModuleName + '/' + INDEX);

        displayBreadCrumb(1, outputModuleName);
        printSnippet(HtmlStr.h1, "Module");
        print("Module name: " + results.getModuleName());

        displayCodeGroupSummaryTable(results);

        displayGraphic(outputModuleName);

        closeHtml();
    }

    private void displayCodeGroupSummaryTable(ModuleResults results) {
        long totalFileSize = results.getTotalFileSize();
        printSnippet(HtmlStr.h2, "Components");
        ResultsList table = results.getCodeGroupSummaryTable();
        if (table.size() == 0) {
            println("None.");
            return;
        }

        startTableHeading();

        tableHeading("", "Circular");
        tableHeading("", "Percent");
        tableHeading("", "Public");
        tableHeading("", "Private");
        tableHeading("", "Package");
        startTableData();
        for (ResultsHolder row : table) {
            displayCodeGroupSummaryRow(row, totalFileSize);
        }

        endTable();
    }

    private void displayCodeGroupSummaryRow(ResultsHolder holder, long totalFileSize) {
        println("<tr>");

        boolean circular = holder.getBool("circular");
        String stateColour = getStateColour( circular);
        tableData(stateColour, yesNo(circular));
        double percent = (holder.getLong("fileSize")/(double)totalFileSize) * 100.0;
        tableData(String.format("%05.2f%%", percent));
        tableData("" + holder.getInt("publicClassesSize"));
        tableData("" + holder.getInt("internalClassesSize"));
        tableData(stateColour, link(DIR_PACKAGES + holder.getString("fullName"), holder.getString("fullDisplayName")));
        println("</tr>");
    }

    private String yesNo(boolean choice) {
        return choice ? "yes" : "no";
    }


    private void generateCodeGroupDetails(ModuleResults results) {
        ResultsList table = results.getPackageInfo();
        for (ResultsHolder row : table) {
            generateCodeGroupHtml(row);
            generateClassesHtml(row);
        }
    }

    private void displayGraphic(String graphicName) {
        printSnippet(HtmlStr.h2, "Dependency Diagram");
        print(image(graphicName, graphicName + ".svg"));
        // <object data="MS-ApplicationInsights-Core.svg" type="image/svg+xml">
        // </object>
    }

    private void generateCodeGroupHtml(ResultsHolder packageHolder) {
        String name = packageHolder.getString("name");
        openHtmlFile(2, outputModuleName, outputModuleName + '/' + DIR_PACKAGES + name);
        displayBreadCrumb(2, getModuleLink(), name);
        printSnippet(HtmlStr.h1, "Component");

        String style = getComponentStyle(packageHolder);
        print("Name: ");
        printBr(fontColour(style, packageHolder.getString("fullDisplayName")));

        print("Circular references: ");

        printBr(fontColour(style, packageHolder.getBool("circular") ? "YES" : "NO"));

        printSnippet(HtmlStr.h2, "Public Classes");
        ResultsList publicClasses = packageHolder.getResultsList("publicClasses");
        if (publicClasses.size() == 0) {
            println("None.");
        }
        for (ResultsHolder row : publicClasses) {
            divComponentOrCircularRef(row);
            printBr(classLink(row.getString("fullClassName"), row.getString("className")));
            divEnd();
        }

        printSnippet(HtmlStr.h2, "Private Classes");
        ResultsList privateClasses = packageHolder.getResultsList("internalClasses");
        if (privateClasses.size() == 0) {
            println("None.");
        }
        for (ResultsHolder row : privateClasses) {
            divComponentOrCircularRef(row);
            printBr(classLink(row.getString("fullClassName"), row.getString("className")));
            divEnd();
        }

        printSnippet(HtmlStr.h2, "Dependencies");
        ResultsList imports = packageHolder.getResultsList("imports");
        if (imports.size() == 0) {
            println("None.");
        }
        for (ResultsHolder row : imports) {
            style = componentOrCircularRefStyle(row);

            print("import " + linkStyle(style, row.getString("name")));
            if (row.getBool("circularLoop")) {
                print(" ------>----- ");
                print(fontColour(style, "HERE"));
            }
            printBr();
        }

        printSnippet(HtmlStr.h2, "Used By (cross reference)");
        ResultsList crossRef = packageHolder.getResultsList("crossRef");
        if (crossRef.size() == 0) {
            println("None.");
        }
        for (ResultsHolder row : crossRef) {
            divColour(row, "circularLoop", "bad", "normal");
            printBr(packageLink(row.getString("name")));
            divEnd();
        }

        closeHtml();
    }

    private String componentOrCircularRefStyle(ResultsHolder item) { // XXXZ
        return getStateColour( item.getBool("circular"));
    }

    private String getStateColour(boolean circular) {
       if (circular) {
            return "bad";
        } else {
            return "good";
        }
    }

    private void divComponentOrCircularRef(ResultsHolder item) {
  if (item.getBool("circular")) {
            divStart("bad");
        } else {
            divStart("good");
        }
    }

    private void generateClassesHtml(ResultsHolder packageHolder) {
        if (!CONFIG_ENABLE_CLASS_OUTPUT) {
            return;
        }
        ResultsList publicClasses = packageHolder.getResultsList("allClasses");
        for (ResultsHolder item : publicClasses) {
            generateClassHtml(item);
        }
    }

    private void generateClassHtml(ResultsHolder item) {
        String classKey = item.getString("classKey");

        openHtmlFile(2, outputModuleName, outputModuleName + '/' + DIR_CLASSES + classKey);

        String className = item.getString("className");
        displayBreadCrumb(2, getModuleLink(), packageLink(item.getString("packageName")), className);

        printSnippet(HtmlStr.h1, "Class");

        String style = getComponentStyle(item);
        print("Name: ");
        printBr(fontColour(style, className));

        print("Circular references: ");

        printBr(fontColour(style, item.getBool("circular") ? "YES" : "NO"));


        printSnippet(HtmlStr.h2, "Dependencies");
        ResultsList importList = item.getResultsList("imports");
        if (importList.size() == 0) {
            println("None.");
        }
        for (ResultsHolder row : importList) {
            style = componentOrCircularRefStyle(row);

            print("import " + linkStyle(style, row.getString("name")));
            if (row.getBool("circularLoop")) {
                print(" ------>----- ");
                print(fontColour(style, "HERE"));
            }
            printBr();
        }

        printSnippet(HtmlStr.h2, "Used by ");
        ResultsList crossRef = item.getResultsList("crossRef");
        if (crossRef.size() == 0) {
            println("None.");
        }
        for (ResultsHolder row : crossRef) {
            style = row.getBool("circularLoop") ? "bad" : "normal";
            printBr("ref " + linkStyle(style, row.getString("name")));
        }

        closeHtml();
    }

}
