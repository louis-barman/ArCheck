package org.archeck.inspect.model;

import org.archeck.inspect.options.ModelOptions;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class ClassAnalyser extends ElementAnalyser {

    private String currentGroupKey;
    protected final ModelOptions options;
    private String groupKey;

    public ClassAnalyser(ModelOptions options) {
        super(options);
        this.options = options;
    }

    @Override
    protected String createElementKey(String packageName, String className) {
        return packageName + SEPARATOR + className;
    }

    @Override
    protected boolean isMemberOfThisGroup(String elementKey) {
        String groupKey = findGroupedNameSpaceHack(elementKey);
        return groupKey.equals(currentGroupKey);
    }

    @Override
    protected void setCurrentElementKey(String elementKey) {
        super.setCurrentElementKey(elementKey);
    }

    public void setGroupKey(String groupKey) {
        currentGroupKey = groupKey;
    }
}
