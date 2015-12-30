package org.archcheck.inspect.scan;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by louisbarman on 20/11/2015.
 */
public class TreeNode {
    public static final String FILE_SEPARATOR = "/";

    private final String packageName;
    private final Map<String, TreeNode> children;
    private boolean theLastNode = false;

    public TreeNode(String packageName) {
        this.packageName = packageName;
        this.children = new TreeMap<String, TreeNode>();
    }

    public TreeNode addChild(String childName) {
        TreeNode childNode = children.get(childName);
        if (childNode != null) {
            return childNode;
        }
        String fullName = (packageName.isEmpty()) ? childName : packageName + FILE_SEPARATOR + childName;

        childNode = new TreeNode(fullName);
        this.children.put(childName, childNode);
        return childNode;
    }

    public void markAsTheLastNode() {
        theLastNode = true;
    }

    public void getChildrenList(NodeResults results , final int depth) {
        if (getNumberOfChildren() == 0) {
            // Always add the final child as we are at the end of the chain
            results.addSinglePackage(packageName);
            return ;
        }

        if (getNumberOfChildren() == 1 && !isLastNode()) {
            TreeNode childNode = children.entrySet().iterator().next().getValue();
            //Ignore packages with one child just move down the change to the next instead
            childNode.getChildrenList(results, depth);
            return ;
        }

        if (depth == 0) {
            if (getNumberOfChildren() >= 2) {
                // More than two children so give up and merge it together
                results.addMergedPackage(packageName);

                return;
            }
        }

        if (isLastNode()) {
            // add a complete package
            results.addSinglePackage(packageName);

        }

        if (depth == 0) {
            return;
        }

        for (Map.Entry<String, TreeNode> child : children.entrySet()) {
            TreeNode childNode = child.getValue();
            childNode.getChildrenList(results, depth - 1);

        }
    }

    public int getNumberOfChildren() {
        return children.size();
    }

    public boolean isLastNode() {
        return theLastNode;
    }

    public String getPackageName() {
        return packageName;
    }
}
