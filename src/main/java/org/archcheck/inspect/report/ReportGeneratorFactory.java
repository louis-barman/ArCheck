package org.archcheck.inspect.report;

import org.archcheck.inspect.output.OutputWrapper;

/**
 * Copyright (C) 2015 Louis Barman.
 */
// TODO rename to RenderOutputFactory and RenderText and RenderHtml
public class ReportGeneratorFactory {
    public static ReportGenerator outputGenerator(String type, OutputWrapper output) {
        String typeString = type.trim();
        if ("html".equalsIgnoreCase(typeString)) {
            return new HtmlGenerator(output);
        }
        if ("GraphViz".equalsIgnoreCase(typeString)) {
            return new GraphVizGenerator(output);
        }
        return new TextGenerator(output);
    }
}
