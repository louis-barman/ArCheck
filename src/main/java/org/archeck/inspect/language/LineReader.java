package org.archeck.inspect.language;

import org.archeck.inspect.util.XLog;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Copyright (C) 2015 Louis Barman on 30/07/15.
 */
public class LineReader {
    private final File file;
    private BufferedReader bufferedReader;

    public LineReader(File file) {

        this.file = file;
    }


    public void openFile() {
        InputStream fis = null;
        try {
            fis = new FileInputStream(file);
            InputStreamReader irs = new InputStreamReader(fis, Charset.forName("UTF-8"));
            bufferedReader = new BufferedReader(irs);

        } catch (FileNotFoundException e) {
            XLog.error(e);
        }
    }

    public String readLine() {
        String text = null;
        try {
            text = bufferedReader.readLine();
        } catch (IOException e) {
            XLog.error(e);
        }
        return text;
    }
}
