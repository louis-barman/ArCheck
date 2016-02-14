package org.archeck.inspect.scan;

import org.archeck.inspect.language.AnyLanguage;
import org.archeck.inspect.language.LanguageFactory;
import org.archeck.inspect.language.LineReader;
import org.archeck.inspect.language.Token;
import org.archeck.inspect.model.GroupControl;
import org.archeck.inspect.util.XLog;

import java.io.File;

/**
 * Created by louisbarman on 02/01/2016.
 */
public class FileTreeScanner implements Token {
    public static final String FILE_SEPARATOR = File.separator;
    private final AnyLanguage language;

    private final PackageTreeScanner packageTreeScanner;

    boolean once = true;
    private LineReader lines;
    private String packageName;
    private int packageStartIndex;

    public FileTreeScanner(GroupControl groupControl) {
        packageTreeScanner = new PackageTreeScanner(groupControl);
        language = LanguageFactory.languageDecoder("java");
    }

    public void addSourceFile(File file) throws AbortScanException {
        if (once) {
            passFile(file);
        }
        String absolutePath = file.getPath();
        String filePath = absolutePath.substring(0, absolutePath.lastIndexOf(FILE_SEPARATOR));
        addSourcePath(filePath);
    }

    public void passFile(File file) {
        lines = new LineReader(file);
        lines.openFile();
        language.passAllLines(lines, this);
        closeFile();
    }

    @Override
    public void foundPackageName(String packageName) {
        this.packageName = packageName;
        closeFile();
    }

    private void closeFile() {
        if (lines != null) {
            lines.closeFile();
            lines = null;
        }
    }

    @Override
    public void foundImport(String packageName, String className) {

    }

    public void addSourcePath(String fullSourcePath) throws AbortScanException {

        if (once) {
            findRootPath(fullSourcePath);
            once = false;
        }

        String sourcePath = stripPath(fullSourcePath);

        packageTreeScanner.addSourcePath(sourcePath);

     }

    private String stripPath(String fullSourcePath) {
        if (packageStartIndex == 0) {
            return fullSourcePath;
        }
        return fullSourcePath.substring(packageStartIndex);
    }

    private void findRootPath(String sourcePath) throws AbortScanException {
        validatePathAndPackageName(sourcePath, packageName);

        packageStartIndex = sourcePath.length() - packageName.length();
    }

    private void validatePathAndPackageName(String sourcePath, String packageName) throws AbortScanException {
        int i1 = packageName.length() - 1;
        int i2 = sourcePath.length() - 1;
        while (i1 >= 0) {
            char c1 = packageName.charAt(i1);
            char c2 = sourcePath.charAt(i2);
            if (c1 != c2 && (c1 != '.' || c2 != '/')) {
                XLog.error("packageName " + packageName + " does not match pathName " + sourcePath);
                throw new AbortScanException();
            }
            i1--;
            i2--;
        }
    }

    public int analyse() {
        return packageTreeScanner.analyse();
    }
}
