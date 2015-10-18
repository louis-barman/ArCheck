package org.archcheck.inspect.report;

import org.archcheck.inspect.output.OutputWrapper;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public abstract class HtmlGeneratorUtils extends ReportGenerator {
    public static final String HTML_EXTENSION = ".html";

    HtmlGeneratorUtils(OutputWrapper output) {
        super(output);
    }

    protected void openHtmlFile(String fileName) {
        openFile(fileName + HTML_EXTENSION);
    }

    protected String getDepthString(int depth) {
        if (depth == 1) {
            return "../";
        }
        if (depth == 2) {
            return "../../";
        }
        return ".";
    }

    protected void openHtmlFile(int depth, String title, String fileName) {
        openHtmlFile(fileName);
        String depthString = getDepthString(depth);
        print(getResourceString("/snippet/start.html", "ArchCheck " + title, depthString));
    }

    protected void closeHtml() {
        print(getResourceString("/snippet/end.html"));
        closeFile();
    }

    protected void printBr(String text) {
        printSnippet(HtmlStr.br, text);
    }

    protected void printBr() {
        printBr("");
    }

    protected String link(String link, String text) {
        return "<a href=\"" + link + HTML_EXTENSION + "\">" + text + "</a>";
    }

    protected String image(String alt, String src) {
        return "<img alt=\"" + alt + "\" src=\"" + src + "\">";
    }

    protected String link(String item) {
        return link(item, item);
    }

    protected String linkStyle(String style, String link, String text) {
        return "<a  class=\"" + style + "\" href=\"" + link + HTML_EXTENSION + "\">" + text + "</a>";
    }

    protected String linkStyle(String style, String text) {
        return linkStyle(style, text, text);
    }

    protected String fontColour(String style, String text) {
        return getSnippet(HtmlStr.font, new String[]{style, text});
    }

    protected void divStart(String classId) {
        println("<div class=\"" + classId + "\">");
    }

    protected void divEnd() {
        println("</div>");
    }

    protected void tableHeading(String style, String heading) {
        println("<th style=\"" + style + "\">" + heading + "</th>");
    }

    protected void startTableHeading() {
        println(" <div class=\"tab\">");
        println("<table><thead>\n<tr>");
    }

    protected void startTableData() {
        println("</tr></thead>\n<tbody>");
    }

    protected void endTable() {
        println("</tbody></table></div>");
    }

    protected void tableData(String style, String text) {
        print("<td class=\"" + style + "\">" + text + "</td>");
    }

    protected void tableData(String text) {
        print("<td>" + text + "</td>");
    }

}
