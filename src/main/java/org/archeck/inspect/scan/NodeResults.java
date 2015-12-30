package org.archeck.inspect.scan;

import org.archeck.inspect.util.XLog;

import java.util.ArrayList;

/**
 * Created by louisbarman on 21/11/2015.
 */
public class NodeResults {
    public static boolean CONFIG_SCAN_TRACE = false;
    private ArrayList<String> mergedPackages = new ArrayList<String> ();
    private int resultsFound = 0;

    public void addMergedPackage(String packageName) {
        if (CONFIG_SCAN_TRACE) {
            XLog.trace("add packageName " + packageName + ".*");
        }
        mergedPackages.add(packageName);
        resultsFound++;
    }

    public void addSinglePackage(String packageName) {
        if (CONFIG_SCAN_TRACE) {
            XLog.trace("add packageName " + packageName);
        }
        resultsFound++;
    }

    public void clear() {
        resultsFound = 0;
        mergedPackages.clear();
    }

    public int resultsFound() {
        return resultsFound;
    }

    public ArrayList<String> getMergedPackages() {
        return mergedPackages;
    }
}
