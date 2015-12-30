package org.archcheck.inspect.model;

import org.archcheck.inspect.options.ModelOptions;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class GroupAnalyser extends PackageAnalyser {


    public GroupAnalyser(ModelOptions options, ElementAnalyser classAnalyser) {
        super(options, classAnalyser);
    }

    @Override
    protected String createElementKey(String packageName, String className) {
        return findGroupedNameSpaceHack(packageName);
    }

    @Override
    public ElementItem addSourceClass(String packageName, String className, SourceStatistics sourceStats) {
        int split = findDepthSplitPoint(packageName);
        if (split > 0) {
            className = getClassNameString(split, packageName, className);
            packageName = getPackageString(split, packageName);
        }

        return super.addSourceClass(packageName, className, sourceStats);
    }

    @Override
    public void addImportedClass(String importedNameSpace, String importedClassName) {
        int split = findDepthSplitPoint(importedNameSpace);
        if (split > 0) {
            importedClassName = getClassNameString(split, importedNameSpace, importedClassName);
            importedNameSpace = getPackageString(split, importedNameSpace);
        }
        super.addImportedClass(importedNameSpace, importedClassName);
    }

    protected String getPackageString(int split, String importedNameSpace) {
        return importedNameSpace.substring(0, split);
    }

    protected String getClassNameString(int split, String importedNameSpace, String importedClassName) {
        return importedNameSpace.substring(split + 1) + SEPARATOR + importedClassName;
    }

    private int findDepthSplitPoint(String text) {
        if (options.getMaxNameSpaceDepth() > 0) {
            int depth = 0;
            for (int splitPoint = 0; splitPoint < text.length(); splitPoint++) {
                if (text.charAt(splitPoint) == SEPARATOR) {
                    depth++;
                    if (depth == options.getMaxNameSpaceDepth()) {
                        return splitPoint;
                    }
                }
            }
        }
        return -1;
    }
}
