package org.archeck.inspect.language;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2015 Louis Barman on 16/08/15.
 */
public class FakeLineReader extends LineReader {

    List<String> inputLines = new ArrayList<String>();

    public FakeLineReader(File file) {
        super(file);
    }

    public void fakeInputLine(String line) {
        inputLines.add(line);

    }

    @Override
    public String readLine() {
        if (inputLines.size() == 0) {
            return null;
        }
        return inputLines.remove(0);

    }
}
