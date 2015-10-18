package org.archcheck.inspect.model;

import org.archcheck.inspect.options.ModelOptions;

import java.util.*;

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

    public ElementItem addSourceFile(String packageName, String className, String filePath, long fileLength) {
        setCurrentElementKey(createElementKey(packageName, className));
        currentElement = getOrCreateElementItem(currentElementKey);
        currentElement.markAsInternal();
        currentElement.setSourceFile(filePath, fileLength);

         return currentElement;
    }


    public ElementItem addSourceClass(String packageName, String className) {

        currentElement.setNameOfElement(packageName, className);

        internalElements.add(currentElement);
        return currentElement;
    }


    protected void setCurrentElementKey(String elementKey) {
        currentElementKey = elementKey;
    }

    protected abstract String createElementKey(String packageName, String className);

    public void addImportedClass(String importedNameSpace, String importedClassName) {

        String elementKey = createElementKey(importedNameSpace, importedClassName);
        if (isMemberOfThisGroup(elementKey)) {
            return;
        }
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


    protected String findGroupedNameSpaceHack(String packageName) {

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


}

