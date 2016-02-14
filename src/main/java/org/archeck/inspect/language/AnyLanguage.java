package org.archeck.inspect.language;

/**
 * Copyright (C) 2015 Louis Barman on 30/07/15.
 */
public abstract class AnyLanguage {

    abstract public void passAllLines(LineReader lines, Token token);

    public abstract String getClassName(String fileName);

    protected boolean beginsWith(String line, String symbol) {
        int symbolLength = symbol.length();
        if (line.length() <= symbolLength) {
            return false;
        }
        if (!line.startsWith(symbol)) {
            return false;
        }
        char nextChar = line.charAt(symbolLength);
        if (nextChar == ' ' || nextChar == '\t') {
            return true;
        }
        return false;

    }

}
