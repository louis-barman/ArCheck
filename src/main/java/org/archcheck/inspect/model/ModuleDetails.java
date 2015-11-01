package org.archcheck.inspect.model;


import org.archcheck.inspect.options.Options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class ModuleDetails {

    private final Options options;
    private String name;

    private final ElementAnalyser classAnalyser;
    private final GroupAnalyser packageAnalyser;
    private Collection<String> pathToSource = new ArrayList<String>();
    private long totalFileLength;

    public ModuleDetails(Options options) {
        this.options = options;
        classAnalyser = new ClassAnalyser(options);
        packageAnalyser = new GroupAnalyser(options, classAnalyser);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModuleName() {
        return name;
    }

    public void addPaths(Collection<String> pathToSource ) {
        this.pathToSource.addAll(pathToSource);
    }

    public void addPath(String pathToSource ) {
        this.pathToSource.add(pathToSource);
    }

    public Collection<String> getSourceDirs() {
        return pathToSource;
    }

    public void analysePhase() {
        packageAnalyser.buildInternalElements();
        packageAnalyser.detectComponents();
        packageAnalyser.detectCircularRefs();
        classAnalyser.buildInternalElements();
        classAnalyser.detectComponents();
        classAnalyser.detectCircularRefs();
    }
    public void addSourceFile(String packageName, String className, String absolutePath, long fileLength) {
        packageAnalyser.addSourceFile(packageName, className, absolutePath, fileLength);
        totalFileLength += fileLength;
    }

    public void addSourceClass(String packageName, String className) {
        packageAnalyser.addSourceClass(packageName, className);
    }

    public void addImportedClass(String importedNameSpace, String importedClassName) {
        if ("R".equals(importedClassName)) {
            return; // Todo Fix android HACK
        }
        packageAnalyser.addImportedClass(importedNameSpace, importedClassName);
    }

    public ResultsList getCodeGroupSummaryTable() {
        return packageAnalyser.getCodeGroupSummaryTable();
    }

    public ResultsList getPackageInfo() {
        return packageAnalyser.getPackageInfo();
    }

    public Options getOptions() {
        return options;
    }

    public long getTotalFileSize() {
        return totalFileLength;
    }
}
