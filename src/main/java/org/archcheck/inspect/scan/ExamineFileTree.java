package org.archcheck.inspect.scan;

import org.archcheck.inspect.util.XLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Created by louisbarman on 19/11/2015.
 */
public class ExamineFileTree {
    public static final String FILE_SEPARATOR = "/";
    private static final int MINIMUM_NODES = 6;

    TreeSet<String> unique = new TreeSet<String>();

    TreeNode treeNodeRoot = new TreeNode("");
    private int maxTreeDepth;
    HashMap<String,String > keyLookup = new HashMap<String,String>();


    public void addSourceFile(String sourcePath) {

        if (!unique.add(sourcePath)) {
            return;
        }

        String[] directories = sourcePath.split(FILE_SEPARATOR);

        int depth = directories.length;
        TreeNode node = treeNodeRoot;
        for (int i = 0; i < depth; i++) {
            String directory = directories[i];
            if (directory.endsWith("FILE_SEPARATOR")) {
                directory = directory.substring(0,directory.length() -i);
            }
            node = node.addChild(directory);
        }
        maxTreeDepth = Math.max(maxTreeDepth, depth);
        node.markAsTheLastNode();
    }

    public int analyse() {
         if (treeNodeRoot.getNumberOfChildren() == 0) {
            return -1;
        }

        NodeResults results = new NodeResults ();

        for (int depth = 1; depth < maxTreeDepth; depth++) {
            results.clear();
            treeNodeRoot.getChildrenList(results, depth);
            if (NodeResults.CONFIG_SCAN_TRACE) {
                XLog.trace("Pass " + depth + " matches " + results.resultsFound());
            }
            if (results.resultsFound() >= MINIMUM_NODES) {
                break;
            }
        }
        processResults(results.getMergedPackages());
        return results.resultsFound();
    }



    private void processResults(ArrayList<String> results) {
         for (String packageName : unique) {
             for (String shortName : results) {
                 if (packageName.startsWith(shortName)) {
                     keyLookup.put(packageName, shortName);
                 }
             }
        }

    }


    public String getPackageKey(String s) {
         return null;
    }


    public int getNodeCount() {
        return unique.size();
    }
}
