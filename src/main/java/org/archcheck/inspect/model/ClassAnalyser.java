package org.archcheck.inspect.model;

import org.archcheck.inspect.options.ModelOptions;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class ClassAnalyser extends ElementAnalyser {

    private String currentGroupKey;
    protected final ModelOptions options;

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
        currentGroupKey = findGroupedNameSpaceHack(elementKey);
    }

}
