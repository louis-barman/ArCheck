package org.archeck.inspect.scan;

import org.archeck.inspect.model.GroupControl;
import org.archeck.inspect.options.Options;
import org.archeck.inspect.util.XLog;

import java.util.*;

/**
 * Created by louisbarman on 02/01/2016.
 */
public class PackageTreeScanner {
    public static final char SEPARATOR = '/';
    public static final int DEPTH_LIMIT = 20;

    private final GroupControl groupControl;
    TreeSet<String> allPackages = new TreeSet();
    TreeSet<String> components = new TreeSet();
    Map<String, List<String>> mergedPackages = new HashMap();


    public PackageTreeScanner(GroupControl groupControl) {
        this.groupControl = groupControl;
    }

    public void addSourcePath(String packagePath) {
        allPackages.add(packagePath);
    }

    public int analyse() {
        if (allPackages.size() < Options.MINIMUM_COMPONENTS) {
            return allPackages.size();
        }

        for (int depth = 1; depth < DEPTH_LIMIT; depth++) {
            mergedPackages.clear();
            components.clear();

            for (String onePackage : allPackages) {
                String componentPackageName = getMaxDepthNameSpace(onePackage, depth);
                components.add(componentPackageName);
                if (componentPackageName.length() <= onePackage.length()) {
                    addToMergedPackageList(mergedPackages, onePackage, componentPackageName);
                }
            }
            if (components.size() >= Options.MINIMUM_COMPONENTS) {
                break;
            }
        }

        printFoundComponents(mergedPackages);

        findMergedPackages();

        return components.size();
    }

    private void addToMergedPackageList(Map<String, List<String>> tbdMap, String onePackage, String componentPackageName) {
        List<String> list = tbdMap.get(componentPackageName);
        if (list == null) {
            list = new ArrayList<String>();
            tbdMap.put(componentPackageName, list);
        }
        list.add(onePackage);
    }

    private void printFoundComponents(Map<String, List<String>> tbdMap) {
        StringBuilder text = new StringBuilder();
        XLog.trace("Found Component");
        for (Map.Entry<String, List<String>> entry : tbdMap.entrySet()) {
            int keyLength = entry.getKey().length();
            text.setLength(0);
            text.append(entry.getKey());
            text.append(" -> ");
            text.append(entry.getValue().size());
            for (String packageName : entry.getValue()) {
                text.append(" ");
                text.append(packageName.substring(keyLength));
            }
            XLog.trace(text.toString());
        }
    }

    private String getMaxDepthNameSpace(String packageName, int depth) {
        int count = 0;
        for (int i = 0; i < packageName.length(); i++) {
            if (packageName.charAt(i) == SEPARATOR) {
                count++;
                if (count >= depth) {
                    return packageName.substring(0, i);
                }
            }
        }

        return packageName;
    }

    private void findMergedPackages() {

        for (Map.Entry<String, List<String>> oneEntry : mergedPackages.entrySet()) {
            findBestComponentName(oneEntry.getValue(), oneEntry.getKey());
        }
    }

    private void findBestComponentName(List<String> matchingPackageNames, String shortName) {
        String savedShortName = findLongestCommonPackageName(matchingPackageNames);

        for (String onePackageName : matchingPackageNames) {
            //XLog.trace("addGroupNameLookup savedShortName " + savedShortName + " packageName " + onePackageName);
            addGroupNameLookup(onePackageName, savedShortName);
        }
    }


    String findLongestCommonPackageName(List<String> packageNames) {

        String targetName = null;
        // first find the shortest package name and use that as the target
        for (String packageName : packageNames) {
            if (targetName == null) {
                targetName = packageName;
            } else {
                if (packageName.length() < targetName.length()) {
                    targetName = packageName;
                }
            }
        }

        // next try short and shorter package names until one is common to all in the list
        while (true) {
            boolean matchFound = true;
            for (String packageName : packageNames) {
                if (!packageName.startsWith(targetName)) {
                    matchFound = false;
                }
            }

            if (!matchFound) {
                int index = targetName.lastIndexOf(SEPARATOR);
                if (index <= 1) {
                    return targetName;
                }
                targetName = targetName.substring(0, index);
            } else {
                return targetName;
            }
        }
    }

    private void addGroupNameLookup(String packageName, String shortName) {
        if (packageName.length() == shortName.length()) {
            //return;
        }
        groupControl.addGroupNameLookup(swapDelimiter(packageName), swapDelimiter(shortName));
    }

    private String swapDelimiter(String path) {
        StringBuilder packageName = new StringBuilder();
        int length = path.length();
        for (int i = 0; i < length; i++) {
            char c = path.charAt(i);
            if (c == '/') {
                c = '.';
            }
            packageName.append(c);
        }
        return packageName.toString();
    }

}
