package org.archcheck.inspect.report;

import org.archcheck.inspect.output.OutputWrapper;
import org.archcheck.inspect.results.ProjectResults;
import org.archcheck.inspect.util.Outcome;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public abstract class ReportGenerator {
    private String resourceFilePath = "";
    private String resourceFileExtension = "";

    protected final OutputWrapper output;
    private boolean abortedFlag;

    ReportGenerator(OutputWrapper output) {
        this.output = output;
    }

    public abstract boolean generateReports(ProjectResults projectResults);

    private void checkOutcome(Outcome outcome) {
        if (!outcome.successful()) {
            abortedFlag = true;
            output.errorPrint(outcome.getMessage());
        }
    }

    protected void println(String s) {
        output.println(s);
    }

    protected void print(String... s) {
        output.print(s);
    }

    protected Outcome openFile(String fileName) {
        if (fileName.contains(" ")) {
            fileName = fileName.replaceAll(" ", "-");
        }
        Outcome outcome = output.openFile(fileName);
        checkOutcome(outcome);
        return outcome;
    }

    protected void closeFile() {
        output.closeFile();
    }


    protected boolean aborted() {
        return abortedFlag;
    }


    protected BufferedReader getResourcesFileString(String resourceFileName) throws FileNotFoundException {
        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();

        String fullResFileName = resourceFileName;

        if (resourceFilePath.length() > 0) {
            fullResFileName = resourceFilePath + "/" + resourceFileName + resourceFileExtension;
        }

        URL fileUrl = classLoader.getResource(fullResFileName);

        FileInputStream fis = new FileInputStream(fileUrl.getFile());
        InputStreamReader irs = new InputStreamReader(fis, Charset.forName("UTF-8"));
        return new BufferedReader(irs);
    }

    protected void printSnippet(String lines, String... args) {
        lines = getSnippet(lines, args);
        println(lines);
    }

    protected String getSnippet(String lines, String[] args) {
        for (int i = 0; i < args.length; i++) {
            String key = "@" + (i + 1) + "@";
            if (lines.contains(key)) {
                lines = lines.replace(key, args[i]);
            }
        }
        return lines;
    }

    public void setResourceFilePath(String resourceFilePath) {
        this.resourceFilePath = resourceFilePath;
    }

    public void setResourceFileExtension(String resourceFileExtension) {
        this.resourceFileExtension = resourceFileExtension;
    }

    protected String getAbsolutePath() {
        return output.getAbsolutePath();
    }

    protected String getResourceString(String resourceFileName, String... replacements) {
        StringBuilder output = new StringBuilder();
        InputStreamReader irs = new InputStreamReader(ReportGenerator.class.getResourceAsStream(resourceFileName), Charset.forName("UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(irs);
        try {
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                line = replaceParameters(replacements, line);
                output.append(line);
                output.append('\n');
            }
        } catch (IOException e) {
            throw new InternalError(e.getMessage());
        }
        return output.toString();
    }


    private String replaceParameters(String[] replacements, String line) {
        if (!line.contains("@")) {
            return line;
        }
        for (int i = 0; i < replacements.length; i++) {
            String key = "@" + (i + 1) + "@";
            if (line.contains(key)) {
                line = line.replace(key, replacements[i]);
            }
        }

        return line;
    }


}
