package org.archcheck.inspect.report;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class HtmlStr {
    public static final String start = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<title>@1@</title>\n" +
            "<link href=\"@2@/css/style.css\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
            "</head>\n" +
            "<body>";

    public static final String end = "</body>\n</html>";

    public static final String p = "<p>@1@</p>";
    public static final String br = "@1@<br>";
    public static final String h1 = "<h1>@1@</h1>";
    public static final String h2 = "<h2>@1@</h2>";
    public static final String h3 = "<h3>@1@</h3>";
    public static final String h4 = "<h4>@1@</h4>";
    public static final String font = "<font class=\"@1@\">@2@</font>";

    public static final String style_sheet = "\n" +
            "body {margin-left:20mm;}" +
            "a { color: #0000bb; }" +
            ".bad, .bad a {\n" +
            "    color: #FF0000;\n" +
            "}\n" +
            ".warn, .warn a {\n" +
            "    color: #FFBB00;\n" +
            "}\n" +
            "\n" +
            ".good, .good a {\n" +
            "    color: #0000FF;\n" +
            "}\n" +
            ".normal, .normal a {\n" +
            "    color: #440000;\n" +
            "}\n";

}
