package org.archeck.inspect.model;

import org.archeck.inspect.options.ModelOptions;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class PackageAnalyser extends ElementAnalyser {

    private final ElementAnalyser classAnalyser;

    public PackageAnalyser(ModelOptions options, ElementAnalyser classAnalyser) {
        super(options);
        this.classAnalyser = classAnalyser;
    }

    @Override
    protected String createElementKey(String packageName, String className) {
        return packageName;
    }

    @Override
    protected ElementItem createElementItem(String elementKey) {
        ElementItem elementItem;
        elementItem = new PackageItem(options, elementKey);
        return elementItem;
    }

    @Override
    public ElementItem addSourceClass(String packageName, String className, SourceStatistics sourceStats) {
        ElementItem classItem = classAnalyser.addSourceClass(packageName, className, sourceStats);
        PackageItem packageItem = (PackageItem) super.addSourceClass(packageName, className, sourceStats);
        packageItem.addInternalClass(classItem);
        return packageItem;
    }

    @Override
    protected void addPackageItemInfo(ResultsHolder results, ElementItem elementItem) {
        super.addPackageItemInfo(results, elementItem);

        PackageItem packageItem = (PackageItem) elementItem;

        packageItem.addInternalClassInfo(results);
    }

}
