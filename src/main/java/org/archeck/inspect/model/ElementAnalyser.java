package org.archeck.inspect.model;

import org.archeck.inspect.options.ModelOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public abstract class ElementAnalyser {
    public static final char SEPARATOR = '.';
    public static final int ITERATION_LIMIT = 200;

    private final Map<String, ElementItem> allElements = new HashMap<String, ElementItem>();
    private final Set<ElementItem> internalElements = new TreeSet<ElementItem>();
    protected final ModelOptions options;
    private String currentElementKey;

    private ElementItem currentElement;

    public ElementAnalyser(ModelOptions options) {

        this.options = options;
    }

    public ElementItem getOrCreateElementItem(String elementKey) {
        ElementItem elementItem = allElements.get(elementKey);
        if (elementItem == null) {
            elementItem = createElementItem(elementKey);
            allElements.put(elementKey, elementItem);
        }
        return elementItem;
    }

    protected ElementItem createElementItem(String elementKey) {
        ElementItem elementItem;
        elementItem = new ElementItem(options, elementKey);
        return elementItem;
    }

    public ElementItem addSourceClass(String packageName, String className, SourceStatistics sourceStats) {
        setCurrentElementKey(createElementKey(packageName, className));
        currentElement = getOrCreateElementItem(currentElementKey);
        currentElement.markAsInternal();
        currentElement.setNameOfElement(packageName, className);
        currentElement.setSourceStats(sourceStats);

        internalElements.add(currentElement);
        return currentElement;
    }


    protected void setCurrentElementKey(String elementKey) {
        currentElementKey = elementKey;
    }

    protected abstract String createElementKey(String packageName, String className);

    public void addImportedClass(String importedNameSpace, String importedClassName) {
        String elementKey = createElementKey(importedNameSpace, importedClassName);
        ElementItem elementItem = getOrCreateElementItem(elementKey);
        currentElement.canSee(elementItem);
        elementItem.addReverseLookUp(currentElement);
    }

    protected boolean isMemberOfThisGroup(String elementKey) {
        return elementKey.equals(currentElementKey);
    }

    public void buildInternalElements() {
        for (ElementItem elementItem : internalElements) {
            elementItem.buildInternalElements();
        }
    }

    public void detectComponents() {
        for (int i = 0; i < ITERATION_LIMIT; i++) {
            boolean found = false;
            for (ElementItem elementItem : internalElements) {

                boolean result = elementItem.detectComponents();
                if (result) {
                    found = true;
                }
            }
            if (!found) {
                break;
            }
        }
    }

    public void detectCircularRefs() {
        for (int i = 0; i < ITERATION_LIMIT; i++) {
            boolean found = false;
            for (ElementItem elementItem : internalElements) {

                boolean result = elementItem.detectCircularRefs();
                if (result) {
                    found = true;
                }
            }
            if (!found) {
                break;
            }
        }
    }

    public ResultsList getCodeGroupSummaryTable() {

        ResultsList resultsList = new ResultsList();

        for (ElementItem elementItem : internalElements) {
            ResultsHolder resultsHolder = new ResultsHolder();
            resultsList.add(resultsHolder);
            elementItem.getCodeGroupSummaryResult(resultsHolder);
        }

        return resultsList;
    }

    public ResultsList getPackageInfo() {

        ResultsList resultsList = new ResultsList();
        for (ElementItem elementItem : internalElements) {
            ResultsHolder results = new ResultsHolder();
            resultsList.add(results);

            addPackageItemInfo(results, elementItem);
        }
        return resultsList;
    }

    protected void addPackageItemInfo(ResultsHolder results, ElementItem elementItem) {
        results.put("name", elementItem.getElementKey());
        results.put("component", elementItem.isComponent());
        results.put("circular", elementItem.isCircularRef());
        results.put("crossRef", elementItem.crossRefsInfo());
        results.put("imports", elementItem.importInfo());
    }


}

