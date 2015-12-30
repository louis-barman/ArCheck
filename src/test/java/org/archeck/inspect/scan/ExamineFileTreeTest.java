package org.archeck.inspect.scan;

import dagger.Module;
import dagger.ObjectGraph;
import org.archeck.inspect.TestBase;
import org.archeck.inspect.app.DaggerAppModule;
import org.archeck.inspect.model.ProjectDetails;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ExamineFileTreeTest extends TestBase {
    private ExamineFileTree classUnderTest;

    // test module override
    @Module(
            includes = DaggerAppModule.class,
            overrides = true,
            injects = {ExamineFileTreeTest.class, Transverse.class, ProjectDetails.class}

    )
    class TestModule {

    }

    // tests
    @Before
    public void setUp() throws Exception {
        ObjectGraph.create(new TestModule()).inject(this);
        classUnderTest = new ExamineFileTree();
    }

    private void fakeFileData(String filePath) {
        classUnderTest.addSourceFile(filePath);
    }

    @Test
    public void confirmSubdirectoriesAreMergedTogether() {
        fakeFileData("x1");
        fakeFileData("x2");
        fakeFileData("x3");
        fakeFileData("x4");
        fakeFileData("x5");
        fakeFileData("x6");
        fakeFileData("a/b/c");
        fakeFileData("a/b/c/d");
        classUnderTest.analyse();
        assertEquals(7, classUnderTest.analyse());
    }

    @Test
    public void confirmOnlyOneDirectoryIsDetected() {
        fakeFileData("a/b/c");
        assertEquals(1, classUnderTest.analyse());
    }

    @Test
    public void confirmSubdirectoriesAreNotMergedTogether() {
        fakeFileData("a/b/c");
        fakeFileData("a/b/c/d");

        assertEquals(2, classUnderTest.analyse());
    }

    @Test
    public void testASimpleTreeHasFourNodes() {
        fakeFileData("a/b/c/e");
        fakeFileData("a/b/c/f");
        fakeFileData("a/b/d/g");
        fakeFileData("a/b/d/h");
        assertEquals(4, classUnderTest.analyse());
    }

    @Test
    public void testASimpleTreeHasMergesSixNodes() {
        fakeFileData("a/b/c");
        fakeFileData("a/b/c/e");
        fakeFileData("a/b/c/f");
        fakeFileData("a/b/d/g");
        fakeFileData("a/b/d/h");
        fakeFileData("a/b/d");
        assertEquals(6, classUnderTest.analyse());
    }

    @Test
    public void testASimpleTreeHasMergesIntoOneNodes() {
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
        assertEquals(9, classUnderTest.analyse());
    }
    @Test
    public void testATwoSimpleTreesHasMerges() {
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
        fakeFileData("com/android/emailcommon");   // merge
        fakeFileData("com/android/emailcommon/internet");
        fakeFileData("com/android/emailcommon/mail");
        fakeFileData("com/android/emailcommon/service");
        fakeFileData("com/android/emailcommon/utility");
        fakeFileData("com/android/mail");  // merge
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
        fakeFileData("com/android/mail/providers");
        fakeFileData("com/android/mail/providers/protos/exchange");
        fakeFileData("com/android/mail/providers/protos/mock");
        fakeFileData("com/android/mail/text");
        fakeFileData("com/android/mail/ui");
        fakeFileData("com/android/mail/ui/settings");
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
        assertEquals(15, classUnderTest.analyse());
    }

}
