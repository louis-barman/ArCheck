package org.archeck.inspect.scan;

import junit.framework.Assert;
import org.archeck.inspect.TestBase;
import org.archeck.inspect.model.FakeGroupControl;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class PackageTreeScannerTest extends TestBase {
    private PackageTreeScanner classUnderTest;
    private FakeGroupControl fakeGroupControl;

    List<String> list = new ArrayList();

    // tests
    @Before
    public void setUp() throws Exception {
        fakeGroupControl = new FakeGroupControl();
        classUnderTest = new PackageTreeScanner(fakeGroupControl);
    }

    @Test
    public void testFindLongestCommonPackageName() {
        list.add("a/b/c/d/f");
        list.add("a/b/c/h/i");
        Assert.assertEquals("a/b/c", classUnderTest.findLongestCommonPackageName(list));
    }

    @Test
    public void testFindLongestCommonPackageNameV2() {
        list.add("a/b/c/d/f");
        list.add("a/b/c");
        Assert.assertEquals("a/b/c", classUnderTest.findLongestCommonPackageName(list));
    }
    @Test
    public void testFindLongestCommonPackageNameV3() {
        list.add("a/b/c/d/f");
        list.add("a/b/c/h/i");
        list.add("a/b/x/d/f");
        list.add("a/b/x/h/i");
        Assert.assertEquals("a/b", classUnderTest.findLongestCommonPackageName(list));
    }
    @Test
    public void testFindLongestCommonPackageNameV4() {
        list.add("org/apache/tools/ant/taskdefs");
        list.add("org/apache/tools/ant/taskdefs/zzz");
         Assert.assertEquals("org/apache/tools/ant/taskdefs", classUnderTest.findLongestCommonPackageName(list));
    }
}

