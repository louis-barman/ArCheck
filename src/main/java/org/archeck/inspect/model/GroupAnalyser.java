package org.archeck.inspect.model;

import org.archeck.inspect.options.ModelOptions;

import java.util.HashMap;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class GroupAnalyser extends PackageAnalyser implements GroupControl {

    private final ClassAnalyser classAnalyser;
    private final HashMap<String, String> keyLookup = new HashMap<String, String>();

    public GroupAnalyser(ModelOptions options, ClassAnalyser classAnalyser) {
        super(options, classAnalyser);
        this.classAnalyser = classAnalyser;
    }

    @Override
    protected String createElementKey(String packageName, String className) {
        return findGroupedNameSpace(packageName);
    }

    @Override
    public ElementItem addSourceClass(String packageName, String className, SourceStatistics sourceStats) {
        String groupPackageName = findGroupedNameSpace(packageName);
        String groupClassName = getClassNameString(groupPackageName, packageName, className);

        classAnalyser.setGroupKey(groupPackageName);

        ElementItem elementItem = super.addSourceClass(groupPackageName, groupClassName, sourceStats);
        if (groupPackageName.length() < packageName.length()) {
            elementItem.addMergedPackageNames(packageName);
        }

        return elementItem;
    }

    @Override
    public void addImportedClass(String importedNameSpace, String importedClassName) {
        String groupPackageName = findGroupedNameSpace(importedNameSpace);
        String groupClassName = getClassNameString(groupPackageName, importedNameSpace, importedClassName);

        if (!isMemberOfThisGroup(groupPackageName)) {
            super.addImportedClass(groupPackageName, groupClassName);
            classAnalyser.addImportedClass(groupPackageName, groupClassName);
        }
    }

    private String getClassNameString(String groupName, String packageName, String className) {
        if (groupName.length() == packageName.length()) {
            return className;
        }
        int split = groupName.length();
        return packageName.substring(split + 1) + SEPARATOR + className;
    }

    protected String findGroupedNameSpace(String packageName) {
        String groupName = keyLookup.get(packageName);
        if (groupName!= null) {
            return groupName;
        }

        return getMaxDepthNameSpace(packageName);
    }

    private String getMaxDepthNameSpace(String packageName) {
        if (options.getMaxNameSpaceDepth() <= 0) {
            return packageName;
        }
        int count = 0;
        for (int i = 0; i < packageName.length(); i++) {
            if (packageName.charAt(i) == SEPARATOR) {
                count++;
                if (count >= options.getMaxNameSpaceDepth()) {
                    return packageName.substring(0, i);
                }
            }
        }

        return packageName;
    }

    @Override
    public void addGroupNameLookup(String packageName, String componentPackageName) {
        keyLookup.put(packageName, componentPackageName );
    }
}
