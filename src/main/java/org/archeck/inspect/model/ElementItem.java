package org.archeck.inspect.model;

import org.archeck.inspect.options.ModelOptions;

import java.util.*;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class ElementItem implements Comparable<ElementItem> {
    protected final String elementKey;
    private final List<ElementItem> canSeeAllImports = new ArrayList<ElementItem>();
    private final List<ElementItem> reverseLookUpImport = new ArrayList<ElementItem>();
    private final Map<String, ElementItem> canSeeInternalImports = new TreeMap<String, ElementItem>();

    private boolean internalElement;
    private boolean componentFlag;
    private boolean notACircularRefFlag;

    protected ModelOptions options;
    private String packageName;
    private String className;
    private SourceStatistics sourceStats;
    private int componentMergeCounter;

    public ElementItem(ModelOptions options, String elementKey) {
        this.options = options;

        this.elementKey = elementKey;
    }

    public void setNameOfElement(String packageName, String className) {

        this.packageName = packageName;
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public void canSee(ElementItem element) {
        canSeeAllImports.add(element);
    }

    public void addReverseLookUp(ElementItem element) {
        reverseLookUpImport.add(element);
    }

    @Override
    public int compareTo(ElementItem o) {
        return (elementKey.compareTo(o.elementKey));
    }

    public String getElementKey() {
        return elementKey;
    }

    public int internalImportsSize() {
        return canSeeInternalImports.size();
    }

    public int reverseLookUpSize() {
        return reverseLookUpImport.size();
    }

    public boolean isInternalElement() {
        return internalElement;
    }

    public void markAsInternal() {
        internalElement = true;
    }

    public Boolean isComponent() {
        return componentFlag;
    }

    boolean isCircularRef() {
        return !notACircularRefFlag && !componentFlag;
    }

    public boolean markAsComponent() {
        if (!componentFlag) {
            componentFlag = true;
            return true;
        }
        return false;
    }

    private boolean markAsNotCircularRef() {
        if (!notACircularRefFlag) {
            notACircularRefFlag = true;
            return true;
        }
        return false;
    }

    public String getFullName() {
        String displayName = elementKey;
        if (componentMergeCounter > 0) {
            // XXX
            //displayName += ".*";
        }
        return displayName;
    }

    // TODO remove
    public boolean isWrapperClass() {
        return !isInternalElement() && reverseLookUpSize() == 1;
    }

    // TODO remove
    public String getWrapperClassName() {
        if (reverseLookUpImport.size() < 1) {
            return null; // TODO Fix this
        }
        return reverseLookUpImport.get(0).elementKey;
    }

    public void buildInternalElements() {
        canSeeInternalImports.clear();
        for (ElementItem elementItem : canSeeAllImports) {
            if (elementItem.isInternalElement()) {
                canSeeInternalImports.put(elementItem.getElementKey(), elementItem);

            }
        }
    }

    // return true to carry on as we have found a component
    public boolean detectComponents() {
        int externalRefs = 0;
        for (Map.Entry<String, ElementItem> elementItemEntry : canSeeInternalImports.entrySet()) {
            ElementItem elementItem = elementItemEntry.getValue();

            if (!elementItem.isComponent()) {
                externalRefs++;
            }

        }
        if (externalRefs == 0) {
            return markAsComponent();
        }
        return false;
    }

    public boolean detectCircularRefs() {
        int externalRefs = 0;
        for (ElementItem elementItem : reverseLookUpImport) {

            if (elementItem.isCircularRef()) {
                externalRefs++;
            }

        }
        if (externalRefs == 0) {
            return markAsNotCircularRef();
        }
        return false;
    }

    private boolean displayProjectPackage(ElementItem visiblePackage, boolean hideInternalComponent) {
        if (options.showAllPackages()) {
            return true;
        }

        if (hideInternalComponent) {
            if (visiblePackage.isComponent()) {
                return false;
            }
        }
        return visiblePackage.isInternalElement();
    }

    public void getCodeGroupSummaryResult(ResultsHolder resultsHolder) {

        resultsHolder.put("fullName", getFullName());
        resultsHolder.put("component", isComponent());
        resultsHolder.put("circular", isCircularRef());


        // TODO delete this code but used by TextGenerator

        ResultsList visiblePackagesList = new ResultsList();
        resultsHolder.put("visiblePackagesList", visiblePackagesList);

        for (Map.Entry<String, ElementItem> elementItemEntry : canSeeInternalImports.entrySet()) {
            ElementItem visiblePackages = elementItemEntry.getValue();
            if (displayProjectPackage(visiblePackages, !options.showComponentPackages())) {

                visiblePackagesList.add(visiblePackages.getElementKey());
            }
        }

    }

    public ResultsList importInfo() {
        Set<String> unique = new HashSet<String>();
        ResultsList resultList = new ResultsList();
        for (Map.Entry<String, ElementItem> elementItemEntry : canSeeInternalImports.entrySet()) {
            ElementItem element = elementItemEntry.getValue();
            if (!unique.contains(element.elementKey)) {
                unique.add(element.elementKey);
                ResultsHolder holder = new ResultsHolder();
                resultList.add(holder);
                holder.put("name", element.elementKey);
                holder.put("circularLoop", element.isCircular(elementKey));
                holder.put("circularWeek", isCircularRef() && element.isCircularRef());
                holder.put("component", element.isComponent());
                holder.put("circularRef", element.isCircularRef());
            }
        }
        return resultList;
    }

    // TODO duplicate code
    public ResultsList crossRefsInfo() {
        Set<String> unique = new HashSet<String>();
        ResultsList resultList = new ResultsList();

        for (ElementItem element : reverseLookUpImport) {
            if (!unique.contains(element.elementKey)) {
                unique.add(element.elementKey);
                ResultsHolder holder = new ResultsHolder();
                resultList.add(holder);
                holder.put("name", element.elementKey);
                holder.put("circularLoop", element.isCircularXRef(this));
                holder.put("component", element.isComponent());
                holder.put("circularRef", element.isCircularRef());
            }

        }
        return resultList;
    }

    private boolean isCircular(String searchKey) {
        return canSeeInternalImports.containsKey(searchKey);
    }

    private boolean isCircularXRef(ElementItem searchItem) {
        return reverseLookUpImport.contains(searchItem);
    }

    public boolean isPrivateElement(String otherElementKey) {
        for (ElementItem element : reverseLookUpImport) {
            if (!element.elementKey.equals(otherElementKey)) {
                return true;
            }
        }
        return false;
    }


    public void setSourceStats(SourceStatistics sourceStats) {
        this.sourceStats = sourceStats;
    }

    public SourceStatistics getSourceStats() {
        return sourceStats;
    }

    public void componentMergeCounter() {
        componentMergeCounter++;

    }
}
