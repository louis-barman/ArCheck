package org.archeck.inspect.model;

import org.archeck.inspect.model.GroupControl;
import org.archeck.inspect.util.XLog;

import java.util.HashMap;

import static junit.framework.TestCase.assertFalse;

/**
 * Created by louisbarman on 01/01/2016.
 */
public class FakeGroupControl implements GroupControl {
    private final HashMap<String, String> keyLookup = new HashMap<String, String>();

    @Override
    public void addGroupNameLookup(String packageName, String componentPackageName) {
        XLog.trace( "packageName " + packageName + " componentPackageName " + componentPackageName);
        assertFalse( keyLookup.containsKey(packageName));
        keyLookup.put(packageName, componentPackageName );
    }

    public String getComponentPackageName(String componentPackageName) {
        return keyLookup.get(componentPackageName);
    }

    public int getComponentPackageNameCount() {
        return keyLookup.size();
    }
}
