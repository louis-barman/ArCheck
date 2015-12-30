package org.archeck.inspect.scan;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.archeck.inspect.model.ModuleDetails;
import org.archeck.inspect.model.ProjectDetails;
import org.archeck.inspect.output.OutputWrapper;
import org.archeck.inspect.report.ReportGenerator;
import org.archeck.inspect.report.ReportGeneratorFactory;
import org.archeck.inspect.results.ProjectResults;
import org.archeck.inspect.util.Outcome;
import org.archeck.inspect.util.XLog;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
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

        String reportDir = configFile.getReportDir();

        if (reportDir != null) {
            Outcome outcome = setHtmlOutput(reportDir);
            if (outcome.failed()) {
                return false;
            }
        }

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
        WildcardFileFilter fileFilter = new WildcardFileFilter(configFile.getFileTypes());

        Collection<String> sourceDirs = module.getSourceDirs();

        for (String sourceDir : sourceDirs) {

            if (!passFilesForOneModule(module, sourceDir, fileFilter)) {
                return false;
            }
        }
        return true;
    }

    private boolean passFilesForOneModule(ModuleDetails module, String sourceDir, WildcardFileFilter fileFilter) {
        Collection<File> files = new ArrayList<File>();
        String projectRootDir = configFile.getProjectRootDir();

        if (!projectRootDir.endsWith("/")) {
            projectRootDir += '/';
        }

        String filePath = projectRootDir + sourceDir;
        File rootDir = new File(filePath);
        if (!rootDir.isDirectory()) {
            XLog.error("The directory does not exist: " + filePath);
            return false;
        }

        files.addAll(FileUtils.listFiles(rootDir, fileFilter, TrueFileFilter.INSTANCE));
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
            boolean success = graphWizReport.generateReports(projectResults);
            if (!success) {
                return false;
            }
            XLog.println("archeck: Generated HTML report in directory '" + configFile.getReportDir() + "'");
        }

        ReportGenerator reports = ReportGeneratorFactory.outputGenerator(reportType, outputWrapper);
        return reports.generateReports(projectResults);
    }

    private Outcome setHtmlOutput(String outputDir) {
        reportType = REPORT_HTML;
        return outputWrapper.setOutputDirectory(outputDir);
    }
}
