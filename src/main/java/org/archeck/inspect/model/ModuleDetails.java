package org.archeck.inspect.model;


import org.archeck.inspect.options.Options;
import org.archeck.inspect.util.XLog;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class ModuleDetails {
    public static final char SEPARATOR = '.'; // todo duplicate

    private final Options options;
    private String name;

    private final ClassAnalyser classAnalyser;
    private final GroupAnalyser groupAnalyser;
    private Collection<String> pathToSource = new ArrayList<String>();
    private long totalFileLength;

    public ModuleDetails(Options options) {
        this.options = options;
        classAnalyser = new ClassAnalyser(options);
        groupAnalyser = new GroupAnalyser(options, classAnalyser);
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
        groupAnalyser.buildInternalElements();
        groupAnalyser.detectComponents();
        groupAnalyser.detectCircularRefs();
        classAnalyser.buildInternalElements();
        classAnalyser.detectComponents();
        classAnalyser.detectCircularRefs();
    }

    public void addSourceClass(String packageName, String className, SourceStatistics sourceStats) {
        if (isHiddenClass(packageName, className)) {
            return;
        }
        groupAnalyser.addSourceClass(packageName, className, sourceStats);
        totalFileLength += sourceStats.getFileSize();
    }

    public void addImportedClass(String importedNameSpace, String importedClassName) {
        if (isHiddenImport(importedNameSpace, importedClassName)) {
            return;
        }
        groupAnalyser.addImportedClass(importedNameSpace, importedClassName);
    }

    public ResultsList getCodeGroupSummaryTable() {
        return groupAnalyser.getCodeGroupSummaryTable();
    }

    public ResultsList getPackageInfo() {
        return groupAnalyser.getPackageInfo();
    }

    public Options getOptions() {
        return options;
    }

    public long getTotalFileSize() {
        return totalFileLength;
    }

    private boolean isHiddenClass(String packageName, String className) {
        final String fullClassName = packageName + SEPARATOR + className;
        if (options.isHiddenClass (fullClassName)) {
            // todo add on HTML
            XLog.w("Ignoring class " + fullClassName );
            return true;
        }
        return false;
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

    public GroupControl getGroupControl() {
        return groupAnalyser;
    }
}
