package org.archeck.inspect.scan;

import org.archeck.inspect.TestBase;
import org.archeck.inspect.model.FakeGroupControl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class FileTreeScannerTest extends TestBase {
    private FileTreeScanner classUnderTest;
    private FakeGroupControl fakeGroupControl;


    // tests
    @Before
    public void setUp() throws Exception {
        fakeGroupControl = new FakeGroupControl();
        classUnderTest = new FileTreeScanner(fakeGroupControl);
    }

    private void fakeFileData(String filePath) {
        try {
            classUnderTest.addSourcePath(filePath);
        } catch (AbortScanException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void confirmSubdirectoriesAreMergedTogether() {
        classUnderTest.foundPackageName("x1");
        fakeFileData("x1");
        fakeFileData("x2");
        fakeFileData("x3");
        fakeFileData("x4");
        fakeFileData("x5");
        fakeFileData("x6");
        fakeFileData("a/b/c");
        fakeFileData("a/b/c/d");
        assertEquals(7, classUnderTest.analyse());
        assertNull( fakeGroupControl.getComponentPackageName("q99"));
        assertEquals("a.b.c", fakeGroupControl.getComponentPackageName("a.b.c"));
        assertEquals("a.b.c", fakeGroupControl.getComponentPackageName("a.b.c.d"));
        assertEquals("x1", fakeGroupControl.getComponentPackageName("x1"));
        assertEquals("x2", fakeGroupControl.getComponentPackageName("x2"));
        assertEquals(8, fakeGroupControl.getComponentPackageNameCount());
    }

    @Test
    public void confirmTwoSubdirectoriesAreMergedTogetherCorrectly() {
        classUnderTest.foundPackageName("x1");
        fakeFileData("x1");
        fakeFileData("x2");
        fakeFileData("x3");
        fakeFileData("x4");
        fakeFileData("x5");
        fakeFileData("x6");
        fakeFileData("a/b/c");
        fakeFileData("a/b/c/d");
        fakeFileData("m/n/o");
        fakeFileData("m/n/o/p");
        assertEquals(8, classUnderTest.analyse());
        assertEquals("a.b.c", fakeGroupControl.getComponentPackageName("a.b.c"));
        assertEquals("a.b.c", fakeGroupControl.getComponentPackageName("a.b.c.d"));
        assertEquals("m.n.o", fakeGroupControl.getComponentPackageName("m.n.o"));
        assertEquals("m.n.o", fakeGroupControl.getComponentPackageName("m.n.o.p"));
        assertEquals("x1", fakeGroupControl.getComponentPackageName("x1"));
        assertEquals("x2", fakeGroupControl.getComponentPackageName("x2"));
        assertEquals(10, fakeGroupControl.getComponentPackageNameCount());
    }

    @Test
    public void confirmOnlyOneDirectoryIsDetected() {
        classUnderTest.foundPackageName("a.b.c");
        fakeFileData("a/b/c");
        assertEquals(1, classUnderTest.analyse());
        assertEquals(0, fakeGroupControl.getComponentPackageNameCount());
    }

    @Test
    public void confirmSubdirectoriesAreNotMergedTogether() {
        classUnderTest.foundPackageName("a.b.c");
        fakeFileData("a/b/c");
        fakeFileData("a/b/c/d");

        assertEquals(2, classUnderTest.analyse());
        assertEquals(0, fakeGroupControl.getComponentPackageNameCount());
    }

    @Ignore
    @Test
    public void testASimpleTreeHasFourNodes() {
        classUnderTest.foundPackageName("a.b.c.e");
        fakeFileData("a/b/c/e");
        fakeFileData("a/b/c/f");
        fakeFileData("a/b/d/g");
        fakeFileData("a/b/d/h");
        assertEquals(4, classUnderTest.analyse());
        assertEquals(4, fakeGroupControl.getComponentPackageNameCount());
    }

    @Test
    public void testASimpleTreeHasMergesSixNodes() {
        classUnderTest.foundPackageName("a.b.c");
        fakeFileData("a/b/c");
        fakeFileData("a/b/c/e");
        fakeFileData("a/b/c/f");
        fakeFileData("a/b/d/g");
        fakeFileData("a/b/d/h");
        fakeFileData("a/b/d");
        assertEquals(6, classUnderTest.analyse());
        assertEquals("a.b.c", fakeGroupControl.getComponentPackageName("a.b.c"));
        assertEquals("a.b.c.e", fakeGroupControl.getComponentPackageName("a.b.c.e"));
        assertEquals("a.b.d.g", fakeGroupControl.getComponentPackageName("a.b.d.g"));
        assertNull(fakeGroupControl.getComponentPackageName("zz"));
        assertEquals(6, fakeGroupControl.getComponentPackageNameCount());
    }

    @Test
    public void testASimpleTreeHasMergesIntoOneNodes() {
        classUnderTest.foundPackageName("x1");
        fakeFileData("x1");
        fakeFileData("x2");
        fakeFileData("x3");
        fakeFileData("z1/z2/z3/x4");
        fakeFileData("x5");
        fakeFileData("x6");
        fakeFileData("x7");
        fakeFileData("x8");
        fakeFileData("a/b/c");
        fakeFileData("a/b/c/e");
        fakeFileData("a/b/c/f");
        fakeFileData("a/b/d/g");
        fakeFileData("a/b/d/h");
        fakeFileData("a/b/d");
        assertEquals(9, classUnderTest.analyse());
        assertEquals("a.b", fakeGroupControl.getComponentPackageName("a.b.c"));
        assertEquals("a.b", fakeGroupControl.getComponentPackageName("a.b.c.e"));
        assertEquals("a.b", fakeGroupControl.getComponentPackageName("a.b.c.f"));
        assertEquals("a.b", fakeGroupControl.getComponentPackageName("a.b.d.g"));
        assertEquals("a.b", fakeGroupControl.getComponentPackageName("a.b.d.h"));
        assertEquals("a.b", fakeGroupControl.getComponentPackageName("a.b.d"));
        assertEquals("x1", fakeGroupControl.getComponentPackageName("x1"));
        assertEquals("z1.z2.z3.x4", fakeGroupControl.getComponentPackageName("z1.z2.z3.x4"));
        assertEquals(14, fakeGroupControl.getComponentPackageNameCount());
    }

    @Test
    public void testATwoSimpleTreesHasMerges() {
        classUnderTest.foundPackageName("x1");
        fakeFileData("x1");
        fakeFileData("x2");
        fakeFileData("x3");
        fakeFileData("x4");
        fakeFileData("x5");
        fakeFileData("x6");
        fakeFileData("x7");
        fakeFileData("x8");
        fakeFileData("a/b/c");
        fakeFileData("a/b/c/e");
        fakeFileData("a/b/c/f");
        fakeFileData("a/b/d/g");
        fakeFileData("a/b/d/h");
        fakeFileData("a/b/d");
        fakeFileData("z/x/y/w/v");
        fakeFileData("z/x/y/w/v/a1");
        fakeFileData("z/x/y/w/v/a2");
        fakeFileData("z/x/y/w/v/a3");
        fakeFileData("z/x/y/w/v/a4");
        fakeFileData("z/x/y/w/v/a5/a");
        assertEquals(10, classUnderTest.analyse());
    }

    @Test
    public void testAlarmClockHasMergesCorrectly() {
        classUnderTest.foundPackageName("android/alarmclock");
        fakeFileData("src/main/java/android/alarmclock");
        // test without "src/main/java/android/deskclock"
        fakeFileData("src/main/java/android/deskclock/alarms");
        fakeFileData("src/main/java/android/deskclock/events");
        fakeFileData("src/main/java/android/deskclock/provider");
        fakeFileData("src/main/java/android/deskclock/stopwatch");
        fakeFileData("src/main/java/android/deskclock/timer");
        //test without "src/main/java/android/deskclock/widget"
        fakeFileData("src/main/java/android/deskclock/widget/selector");
        fakeFileData("src/main/java/android/deskclock/widget/sgv");
        fakeFileData("src/main/java/android/deskclock/worldclock");
        assertEquals(8, classUnderTest.analyse());
    }

    @Test
    public void testAlarmClock2MergesCorrectly() {
        classUnderTest.foundPackageName("android/alarmclock");
        fakeFileData("src/main/java/android/alarmclock");
        fakeFileData("src/main/java/android/deskclock");
        fakeFileData("src/main/java/android/deskclock/alarms");
        fakeFileData("src/main/java/android/deskclock/events");
        fakeFileData("src/main/java/android/deskclock/provider");
        fakeFileData("src/main/java/android/deskclock/stopwatch");
        fakeFileData("src/main/java/android/deskclock/timer");
        fakeFileData("src/main/java/android/deskclock/widget");
        fakeFileData("src/main/java/android/deskclock/widget/selector");
        fakeFileData("src/main/java/android/deskclock/widget/sgv");
        fakeFileData("src/main/java/android/deskclock/worldclock");
        assertEquals(9, classUnderTest.analyse());
    }

    @Test
    public void testUnifiedEmailMergesCorrectly() {
        classUnderTest.foundPackageName("com/android/emailcommon");
        fakeFileData("com/android/emailcommon");
        fakeFileData("com/android/emailcommon/internet");
        fakeFileData("com/android/emailcommon/mail");
        fakeFileData("com/android/emailcommon/service");
        fakeFileData("com/android/emailcommon/utility");
        fakeFileData("com/android/mail");
        fakeFileData("com/android/mail/analytics");
        fakeFileData("com/android/mail/bitmap");
        fakeFileData("com/android/mail/browse");
        fakeFileData("com/android/mail/compose");
        fakeFileData("com/android/mail/content");
        fakeFileData("com/android/mail/drawer");
        fakeFileData("com/android/mail/graphics");
        fakeFileData("com/android/mail/perf");
        fakeFileData("com/android/mail/photo");
        fakeFileData("com/android/mail/photomanager");
        fakeFileData("com/android/mail/preferences");
        fakeFileData("com/android/mail/print");
        fakeFileData("com/android/mail/providers");   // merge
        fakeFileData("com/android/mail/providers/protos/exchange");  // merge
        fakeFileData("com/android/mail/providers/protos/mock");  // merge
        fakeFileData("com/android/mail/text");
        fakeFileData("com/android/mail/ui"); // merge
        fakeFileData("com/android/mail/ui/settings"); // merge
        fakeFileData("com/android/mail/utils");
        fakeFileData("com/android/mail/widget");
        fakeFileData("com/google/android/mail/common/base");   // merge
        fakeFileData("com/google/android/mail/common/html/parser");   // merge
        fakeFileData("org/apache/commons/io");  // merge
        fakeFileData("org/apache/commons/io/comparator");  // merge
        fakeFileData("org/apache/commons/io/filefilter");  // merge
        fakeFileData("org/apache/commons/io/input");  // merge
        fakeFileData("org/apache/commons/io/output");  // merge
        fakeFileData("org/apache/james/mime4j");   // merge
        fakeFileData("org/apache/james/mime4j/codec");  // merge
        fakeFileData("org/apache/james/mime4j/decoder"); // merge
        fakeFileData("org/apache/james/mime4j/field"); // merge
        fakeFileData("org/apache/james/mime4j/field/address");
        fakeFileData("org/apache/james/mime4j/field/address/parser");
        fakeFileData("org/apache/james/mime4j/field/contenttype/parser");
        fakeFileData("org/apache/james/mime4j/field/datetime");
        fakeFileData("org/apache/james/mime4j/field/datetime/parser");
        fakeFileData("org/apache/james/mime4j/message"); // merge
        fakeFileData("org/apache/james/mime4j/util");// merge
        assertEquals(26, classUnderTest.analyse());
    }

    @Test
    public void testAntMergesCorrectly() {
        classUnderTest.foundPackageName("org.apache.tools.ant");
        fakeFileData("org/apache/tools/ant");
        fakeFileData("org/apache/tools/ant/attribute");
        fakeFileData("org/apache/tools/ant/dispatch");
        fakeFileData("org/apache/tools/ant/filters");
        fakeFileData("org/apache/tools/ant/filters/util");
        fakeFileData("org/apache/tools/ant/helper");
        fakeFileData("org/apache/tools/ant/input");
        fakeFileData("org/apache/tools/ant/launch");
        fakeFileData("org/apache/tools/ant/listener");
        fakeFileData("org/apache/tools/ant/loader");
        fakeFileData("org/apache/tools/ant/property");
        fakeFileData("org/apache/tools/ant/taskdefs");
        fakeFileData("org/apache/tools/ant/taskdefs/zzz");
        fakeFileData("org/apache/tools/ant/types");
        fakeFileData("org/apache/tools/ant/util");
        fakeFileData("org/apache/tools/ant/util/zzz");
        assertEquals(13, classUnderTest.analyse());
        assertEquals(16, fakeGroupControl.getComponentPackageNameCount());
        assertEquals("org.apache.tools.ant.taskdefs", fakeGroupControl.getComponentPackageName("org.apache.tools.ant.taskdefs.zzz"));
        assertEquals("org.apache.tools.ant.filters", fakeGroupControl.getComponentPackageName("org.apache.tools.ant.filters.util"));
        assertEquals("org.apache.tools.ant.util", fakeGroupControl.getComponentPackageName("org.apache.tools.ant.util.zzz"));
    }

    @Test
    public void testPathNamesAreConvertToPackageNames() {
        classUnderTest.foundPackageName("android.alarmclock");
        fakeFileData("src/main/java/android/alarmclock");
        fakeFileData("src/main/java/android/deskclock");
        fakeFileData("src/main/java/android/deskclock/alarms");
        fakeFileData("src/main/java/android/deskclock/events");
        fakeFileData("src/main/java/android/deskclock/provider");
        fakeFileData("src/main/java/android/deskclock/stopwatch");
        fakeFileData("src/main/java/android/deskclock/timer");
        fakeFileData("src/main/java/android/deskclock/widget");
        fakeFileData("src/main/java/android/deskclock/widget/selector");
        fakeFileData("src/main/java/android/deskclock/widget/sgv");
        fakeFileData("src/main/java/android/deskclock/worldclock");
        assertEquals(9, classUnderTest.analyse());
        assertEquals("android.deskclock.widget", fakeGroupControl.getComponentPackageName("android.deskclock.widget"));
        assertEquals("android.deskclock.widget", fakeGroupControl.getComponentPackageName("android.deskclock.widget.selector"));
        assertEquals("android.deskclock.widget", fakeGroupControl.getComponentPackageName("android.deskclock.widget.sgv"));
        assertEquals(11, fakeGroupControl.getComponentPackageNameCount());
    }

    @Test(expected = AbortScanException.class)
    public void testBadPackageNameIsRejected() throws Exception {
        classUnderTest.foundPackageName("android.other");
        classUnderTest.addSourcePath("src/main/java/android/alarmclock");
    }

    @Test
    public void confirmDuplicateComponentNamesAreRemoved() {
        classUnderTest.foundPackageName("org/any/a/x1");
        fakeFileData("org/any/a/x1");
        fakeFileData("org/any/a/dup1");
        fakeFileData("org/any/a/c/d/x3");
        fakeFileData("org/any/a/x4/sub1");
        fakeFileData("org/any/a/x4/sub2");
        fakeFileData("org/any/b/x5");
        fakeFileData("org/any/b/dup2");
        fakeFileData("org/any/b/x7");
        fakeFileData("org/any/b/x9");
        classUnderTest.analyse();
    }
}
