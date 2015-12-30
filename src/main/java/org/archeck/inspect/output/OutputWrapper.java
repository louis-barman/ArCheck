package org.archeck.inspect.output;

import org.apache.commons.io.FileUtils;
import org.archeck.inspect.util.Outcome;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Copyright (C) 2015 Louis Barman.
 */
@Singleton
public class OutputWrapper {
    File outputDir;
    PrintStream currentFileOutput = System.out;

    @Inject
    public OutputWrapper() {

    }

    public void println(String... s) {
        for (Object obj : s) {
            currentFileOutput.println(obj);
        }
    }

    public void print(String... s) {
        for (Object obj : s) {
            currentFileOutput.print(obj);
        }
    }

    public Outcome openFile(String pathAndFilename) {

        try {

            createAnyDirectory(pathAndFilename);

            File file = new File(outputDir.getPath() + "/" + pathAndFilename);

            if (file.exists()) {
                boolean success = file.delete();
                if (!success) {
                    return Outcome.failure("remove file name: " + pathAndFilename);
                }
            }

            if (!file.createNewFile()) {
                return Outcome.failure("Cannot create file: " + pathAndFilename);
            }

            currentFileOutput = new PrintStream(file);
        } catch (FileNotFoundException e) {
            return Outcome.failure(e);
        } catch (IOException e) {
            return Outcome.failure(e);
        }

        return Outcome.success();
    }

    private void createAnyDirectory(String pathAndFilename) throws IOException {
        int index = pathAndFilename.lastIndexOf('/');
        if (index > 1) {
            File dir = new File(outputDir.getPath() + "/" + pathAndFilename.substring(0, index));
            dir.mkdirs();
        }
    }

    public void closeFile() {
        currentFileOutput.close();
        currentFileOutput = null;
    }

    public Outcome setOutputDirectory(String outputPath) {
        outputDir = new File(outputPath);
        if (outputDir.isDirectory()) {
            return Outcome.success();
        }
        boolean success = outputDir.mkdirs();
        if (!success) {
            return Outcome.failure("Cannot create directory: " + outputPath);
        }

        return Outcome.success();
    }

    public Outcome deleteOutputDirectory() {
        if (outputDir == null) {
            return Outcome.success();
        }

        try {
            FileUtils.deleteDirectory(outputDir);
        } catch (IOException e) {
            return Outcome.failure(e);
        }
        return Outcome.success();
    }

    public void errorPrint(String message) {
        System.out.println(message);
    }

    public String getAbsolutePath() {
        return outputDir.getAbsolutePath();
    }

}
