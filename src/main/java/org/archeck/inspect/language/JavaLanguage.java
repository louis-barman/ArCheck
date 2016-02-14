package org.archeck.inspect.language;

import org.archeck.inspect.util.XLog;

/**
 * Assumptions:
 * All classes start with a capital letter and all package names are lowercase.
 * there are no methods or variables the have the same name as a class
 * (Eg 'MyClass String;' <- String is a variable not the String Class.)
 */

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class JavaLanguage extends AnyLanguage {
    public static final String KEY_WORD_PACKAGE = "package";
    public static final String KEY_WORD_IMPORT = "import";
    public static final String KEY_WORD_STATIC = "static";
    public static final String STATEMENT_TERMINATOR = ";";

    public void passAllLines(LineReader lines, Token token) {
        String line;


        while ((line = lines.readLine()) != null) {
            passOneLine(line, token);
        }

    }

    private void passOneLine(String line, Token token) {
        line = line.trim();
        if (beginsWith(line, KEY_WORD_PACKAGE)) {
            if (isWhiteSpace(line, KEY_WORD_PACKAGE.length())) {
                XLog.d("package: " + line);
                String packageName = line.substring(KEY_WORD_PACKAGE.length()).trim();
                packageName = trimLineEnding(packageName);
                token.foundPackageName(packageName);
            }
        }
        if (beginsWith(line, KEY_WORD_IMPORT)) {
            XLog.d("import: " + line);
            String importName = line.substring(KEY_WORD_IMPORT.length() + 1).trim();

            if (importName.startsWith(KEY_WORD_STATIC)) {
                importName = importName.substring(KEY_WORD_STATIC.length() + 1).trim();
            }

            int index = importName.lastIndexOf('.');
            if (index <= 0) {
                return;
            }
            if (index + 1 >= importName.length()) {
                return;
            }
            String importPackageName = importName.substring(0, index);
            String className = importName.substring(index + 1, importName.length());
            className = trimLineEnding(className);
            token.foundImport(importPackageName, className);
        }
    }

    @Override
    public String getClassName(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index <= 0) {
            XLog.internalError("Missing file extension");
            return "Unknown";
        }
        return fileName.substring(0, index);
    }

    private boolean isWhiteSpace(String line, int offset) {
        if (line.length() <= offset) {
            return false;
        }
        char c = line.charAt(offset);
        return c == ' ' || c == '\t';
    }

    // default for TEST only
    String trimLineEnding(String text) {
        if (text.endsWith(STATEMENT_TERMINATOR)) {
            text = text.substring(0, text.length() - 1).trim();
        }
        return text;
    }

}
