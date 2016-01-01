package org.archeck.inspect.model;

import org.archeck.inspect.options.ModelOptions;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class GroupAnalyser extends PackageAnalyser {


    private final ClassAnalyser classAnalyser;

    public GroupAnalyser(ModelOptions options, ClassAnalyser classAnalyser) {
        super(options, classAnalyser);
        this.classAnalyser = classAnalyser;
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

        classAnalyser.setGroupKey(packageName);

        ElementItem elementItem = super.addSourceClass(packageName, className, sourceStats);
        if (split > 0) {
            elementItem.componentMergeCounter();
        }

        return elementItem;
    }

    @Override
    public void addImportedClass(String importedNameSpace, String importedClassName) {
        int split = findDepthSplitPoint(importedNameSpace);
        if (split > 0) {
            importedClassName = getClassNameString(split, importedNameSpace, importedClassName);
            importedNameSpace = getPackageString(split, importedNameSpace);
        }
        if (!isMemberOfThisGroup(importedClassName)) {
            super.addImportedClass(importedNameSpace, importedClassName);
            classAnalyser.addImportedClass(importedNameSpace, importedClassName);
        }
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
