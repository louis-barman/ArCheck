package org.archcheck.inspect.options;

import java.util.*;

/**
 * Copyright (C) 2015 Louis Barman.
 */

public class Options implements ModelOptions {
    public boolean showAllPackages = false;
    public boolean showComponentPackages = false;
    public int maxNameSpaceDepth = 0;

    private final List<String> hiddenImports = new ArrayList<String>();

    Map<String, Boolean> hiddenClasses = new HashMap<String, Boolean>();

    public boolean showAllPackages() {
        return showAllPackages;
    }

    @Override
    public int getMaxNameSpaceDepth() {
        return maxNameSpaceDepth;
    }

    public boolean showComponentPackages() {
        return showComponentPackages;
    }


    public boolean isHiddenClass(String s) {

        Boolean value = hiddenClasses.get(s);
        if (value == null) {
            return false;
        }
        if (!value) {
            hiddenClasses.put(s, true);
        }

        return true;
    }

    public void addHiddenClass(String s) {
        hiddenClasses.put(s, false);
    }

    public void addHiddenImport(String className) {
        hiddenImports.add(className);
    }

    public List<String> getHiddenImports() {
        return Collections.unmodifiableList(hiddenImports);
    }

    public void setMaxDepth(int maxDepth) {
        maxNameSpaceDepth = maxDepth;
    }
}
