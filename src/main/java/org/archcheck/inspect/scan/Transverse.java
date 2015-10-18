package org.archcheck.inspect.scan;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.archcheck.inspect.model.ModuleDetails;
import org.archcheck.inspect.model.ProjectDetails;
import org.archcheck.inspect.output.OutputWrapper;
import org.archcheck.inspect.report.ReportGenerator;
import org.archcheck.inspect.report.ReportGeneratorFactory;
import org.archcheck.inspect.results.ProjectResults;
import org.archcheck.inspect.util.Outcome;
import org.archcheck.inspect.util.XLog;

import javax.inject.Inject;
import java.io.File;
import java.util.Collection;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class Transverse {

    public static final String REPORT_HTML = "html";
    private String reportType = "txt";

    @Inject
    ConfigurationFile configFile;

    @Inject
    ProjectResults projectResults;

    @Inject
    ProjectDetails projectDetails;

    @Inject
    OutputWrapper outputWrapper;


    public boolean start() {

        for (ModuleDetails module : projectDetails.getModuleList()) {
            projectResults.createModuleResults(module);
            boolean result = findFilesPhase(module);
            if (!result) {
                return false;
            }
        }
        projectDetails.analysePhase();

        return resultsPhase();
    }

    private boolean findFilesPhase(ModuleDetails module) {
        String sourceDir = module.getSourceDir();

        WildcardFileFilter fileFilter = new WildcardFileFilter(configFile.getFileTypes());

        String projectRootDir = configFile.getProjectRootDir();
        if (!projectRootDir.endsWith("/")) {
            projectRootDir += '/';
        }
        String filePath = projectRootDir + sourceDir;
        File rootDir = new File(filePath);
        if (!rootDir.isDirectory()) {
            XLog.error(" The directory does not exist: " + filePath);
            return false;
        }

        Collection<File> files = FileUtils.listFiles(rootDir, fileFilter, TrueFileFilter.INSTANCE);
        for (File file : files) {
            XLog.v("Found file: " + file);

            SourceFile sourceFile = new SourceFile(file, module);

            sourceFile.passFile();
        }
        return true;
    }

    private boolean resultsPhase() {
        Outcome result = outputWrapper.deleteOutputDirectory();
        if (!result.successful()) {
            return false;
        }

        if (reportType.equals(REPORT_HTML)) {
            ReportGenerator graphWizReport = ReportGeneratorFactory.outputGenerator("GraphViz", outputWrapper);
            graphWizReport.generateReports(projectResults);
        }

        ReportGenerator reports = ReportGeneratorFactory.outputGenerator(reportType, outputWrapper);
        return reports.generateReports(projectResults);
    }

    public Outcome setHtmlOutput(String outputDir) {
        reportType = REPORT_HTML;
        return outputWrapper.setOutputDirectory(outputDir);

    }
}
