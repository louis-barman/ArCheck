package org.archeck.inspect.model;

import org.archeck.inspect.options.ModelOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class PackageItem extends ElementItem {

    private final List<ElementItem> internalClassList = new ArrayList<ElementItem>();

    public PackageItem(ModelOptions options, String elementKey) {
        super(options, elementKey);
    }

    @Override
    public void setNameOfElement(String packageName, String className) {
        super.setNameOfElement(packageName, packageName);
    }

    public void addInternalClass(ElementItem classItem) {
        internalClassList.add(classItem);
    }

    public void addInternalClassInfo(ResultsHolder results) {
        ResultsList resultList = getAllInternalClassesInfo();
        results.put("allClasses", resultList);

        ResultsList publicClassResults = new ResultsList();
        results.put("publicClasses", publicClassResults);
        ResultsList internalClassesResults = new ResultsList();
        results.put("internalClasses", internalClassesResults);

        for (ElementItem classItem : internalClassList) {
            ResultsHolder classItemResults = new ResultsHolder();
            classItemResults.put("fullClassName", classItem.getFullName());
            classItemResults.put("className", classItem.getClassName());
            classItemResults.put("component", classItem.isComponent());
            classItemResults.put("circularRef", classItem.isCircularRef());
            if (classItem.isPrivateElement(elementKey)) {
                publicClassResults.add(classItemResults);
            } else {
                internalClassesResults.add(classItemResults);
            }
        }
    }

    private ResultsList getAllInternalClassesInfo() {
        ResultsList resultList = new ResultsList();
        for (ElementItem classItem : internalClassList) {
            ResultsHolder classItemResults = new ResultsHolder();
            resultList.add(classItemResults);
            classItemResults.put("classKey", classItem.getElementKey());
            classItemResults.put("className", classItem.getClassName());
            classItemResults.put("packageName", classItem.getPackageName());
            classItemResults.put("component", classItem.isComponent());
            classItemResults.put("crossRef", classItem.crossRefsInfo());
            classItemResults.put("imports", classItem.importInfo());
        }

        return resultList;
    }

    @Override
    public void getCodeGroupSummaryResult(ResultsHolder resultsHolder) {
        super.getCodeGroupSummaryResult(resultsHolder);

        int privateCounter = 0;
        int publicCounter = 0;
        long totalFileSize = 0;
        for (ElementItem classItem : internalClassList) {
            if (classItem.isPrivateElement(elementKey)) {
                privateCounter++;
            } else {
                publicCounter++;
            }
            totalFileSize += classItem.getSourceStats().getFileSize();
        }
        resultsHolder.put("publicClassesSize", privateCounter);
        resultsHolder.put("internalClassesSize", publicCounter);
        resultsHolder.put("fileSize", totalFileSize);
    }

}
