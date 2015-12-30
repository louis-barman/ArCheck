package org.archeck.inspect.language;

import org.archeck.inspect.TestBase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class JavaLanguageTest extends TestBase {

    JavaLanguage classUnderTest = new JavaLanguage();
    Token token = Mockito.mock(Token.class);
    FakeLineReader lineReader = new FakeLineReader(null);

    @Before
    public void setUp() throws Exception {

    }

    public void fakeInputLine(String line) {
        lineReader.fakeInputLine(line);
    }

    @Test
    public void test_trimLineEnding() {
        assertEquals("AA", classUnderTest.trimLineEnding("AA;"));
        assertEquals("BB;", classUnderTest.trimLineEnding("BB;;"));
        assertEquals("CC", classUnderTest.trimLineEnding("CC"));
    }

    @Test
    public void test_foundImportIsNotCalled() {
        fakeInputLine("any line;");
        classUnderTest.findImports(lineReader, token);
        verify(token, never()).foundImport(anyString(), anyString());
        verify(token, never()).foundPackageName(anyString());
    }

    @Test
    public void importStatementInASingleLineCommentIsIgnored() {
        fakeInputLine("//import package.name.Class;");
        classUnderTest.findImports(lineReader, token);
        verify(token, never()).foundImport(anyString(), anyString());
    }

    /*
    // TODO uncomment
    @Test
    public void importStatementInAMultiLineCommentIsIgnored() {

        fakeInputLine("/*");
        fakeInputLine("import package.name.Class;");
        fakeInputLine("* /"); fixme
        classUnderTest.findImports(lineReader, token);
        verify(token, never()).foundImport(anyString(), anyString());
    }
    */


    @Test
    public void importStatementIsSplitIntoIntoPackageAndClass() {
        fakeInputLine("import package.name.Class;");
        classUnderTest.findImports(lineReader, token);
        verify(token).foundImport("package.name", "Class");
    }

    @Test
    public void staticImportStatementWorksCorrectly() {
        fakeInputLine("import static package.name.Class;");
        classUnderTest.findImports(lineReader, token);
        verify(token).foundImport("package.name", "Class");
    }

    @Test
    public void twoImportStatementsWorkCorrectly() {
        fakeInputLine("import package.name.Class;");
        fakeInputLine("import package.name2.Class2;");
        classUnderTest.findImports(lineReader, token);
        verify(token, times(2)).foundImport(anyString(), anyString());
        verify(token).foundImport("package.name", "Class");
        verify(token).foundImport("package.name2", "Class2");
    }

    /*
    TODO uncomment
    @Test
    public void twoImportStatementsWorkCorrectlyOnOneLine() {
        fakeInputLine("import package.name.Class;import package.name2.Class2;");
        classUnderTest.findImports(lineReader, token);
        verify(token, times(2)).foundImport(anyString(), anyString());
        verify(token).foundImport("package.name", "Class");
        verify(token).foundImport("package.name2", "Class2");
    }

    @Test
    public void importClassesStartWithFirstCapitalLetter() {
        fakeInputLine("import package.name.Class.Enum;");
        classUnderTest.findImports(lineReader, token);
        verify(token).foundImport("package.name", "Class.Enum");
    }
    */


}
