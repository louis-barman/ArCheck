package org.archeck.inspect.scan;

import org.archeck.inspect.language.AnyLanguage;
import org.archeck.inspect.language.LanguageFactory;
import org.archeck.inspect.language.LineReader;
import org.archeck.inspect.language.Token;
import org.archeck.inspect.model.ModuleDetails;
import org.archeck.inspect.model.SourceStatistics;
import org.archeck.inspect.util.XLog;

import java.io.File;

/**
 * Copyright (C) 2015 Louis Barman.
 */
/*
todo ignore imports in comments
*/

public class SourceFile implements Token {
    private final File file;
    private final ModuleDetails module;
    private final AnyLanguage language;
    private String className;
    private String packageName;

    public SourceFile(File file, ModuleDetails module) {
        this.file = file;
        this.module = module;
        language = LanguageFactory.languageDecoder("java");
    }

    public void passFile() {
        LineReader lines = new LineReader(file);
        lines.openFile();
        className = language.getClassName(file.getName());
        language.passAllLines(lines, this);
        lines.closeFile();
    }

    @Override
    public void foundPackageName(String packageName) {
        this.packageName = packageName;

        SourceStatistics sourceStats = new SourceStatistics(file.getAbsolutePath(), file.length());
        module.addSourceClass(packageName, className, sourceStats);
    }

    @Override
    public void foundImport(String importedNameSpace, String importedClass) {
        if (packageName == null) {
            XLog.internalError("Import found before package name");
            return;
        }

        module.addImportedClass(importedNameSpace, importedClass);
    }

}
