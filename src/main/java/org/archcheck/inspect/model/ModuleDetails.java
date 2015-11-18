package org.archcheck.inspect.model;


import org.archcheck.inspect.options.Options;
import org.archcheck.inspect.util.XLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class ModuleDetails {
    public static final char SEPARATOR = '.'; // todo duplicate

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
        if (isHiddenClassLog(packageName, className)) {
            return;
        }
        packageAnalyser.addSourceFile(packageName, className, absolutePath, fileLength);
        totalFileLength += fileLength;
    }

    public void addSourceClass(String packageName, String className) {
        if (isHiddenClass(packageName, className)) {
            return;
        }
        packageAnalyser.addSourceClass(packageName, className);
    }

    public void addImportedClass(String importedNameSpace, String importedClassName) {
        if (isHiddenImport(importedNameSpace, importedClassName)) {
            return;
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

    private boolean isHiddenClassLog(String packageName, String className) {
        if (isHiddenClass (packageName, className)) {
            // todo add on HTML
            XLog.w("Ignoring class " + packageName + SEPARATOR +className );
            return true;
        }
        return false;
    }

    private boolean isHiddenClass(String packageName, String className) {
        return options.isHiddenClass(packageName + SEPARATOR +className );

    }

    private boolean isHiddenImport(String importedNameSpace, String importedClassName) {
        if (options.isHiddenImport(importedNameSpace + SEPARATOR + importedClassName )) {
            // todo add on HTML
            XLog.w("Ignoring Import " + importedNameSpace + SEPARATOR + importedClassName );
            return true;
        }
        if ("R".equals(importedClassName)) {
            //XLog.w("Ignoring Import " + importedNameSpace + SEPARATOR + importedClassName );
            return true; // Todo Fix android HACK
        }
        return false;
    }

}
