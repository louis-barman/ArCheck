package org.archeck.inspect.language;

/**
 * Copyright (C) 2015 Louis Barman on 02/08/15.
 */
public interface Token {

    void foundPackageName(String name);

    void foundImport(String packageName, String className);
}
