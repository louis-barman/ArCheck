package org.archeck.inspect.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Copyright (C) 2015 Louis Barman.
 */

public class Options implements ModelOptions {
    public boolean showAllPackages = false;
    public boolean showComponentPackages = false;
    public int maxNameSpaceDepth = 0;

    private final List<String> hiddenImports = new ArrayList<String>();

    private final List<String> hiddenClasses = new ArrayList<String>();

    public boolean showAllPackages() {
        return showAllPackages;
    }

    public int getMaxNameSpaceDepth() {
        return maxNameSpaceDepth;
    }

    public boolean showComponentPackages() {
        return showComponentPackages;
    }


    public void addHiddenClasses(Collection<String> strings) {
        for (String s : strings) {
            hiddenClasses.add(s);
        }
    }

    public void addHiddenImports(Collection<String> className) {
        for (String s : className) {
            hiddenImports.add(s);
        }
    }

    public boolean isHiddenImport(String fullClassName) {
        if (isClassMatch(hiddenImports, fullClassName)) {
            return true;
        }

        return false;
    }

    public boolean isHiddenClass(String fullClassName) {
        if (isClassMatch(hiddenClasses, fullClassName)) {
            return true;
        }

        return false;
    }

    private boolean isClassMatch(List<String> matchList, String name) {
        for (String matchItem : matchList) {
            if (matchItem.endsWith(".*")) {
                matchItem = matchItem.substring(0, matchItem.length() - ".*".length());
                if (name.startsWith(matchItem)) {
                    return true;
                }
            }
            if (name.equals(matchItem)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getHiddenImports() {
        return Collections.unmodifiableList(hiddenImports);
    }

    public void setMaxDepth(int maxDepth) {
        maxNameSpaceDepth = maxDepth;
    }
}
