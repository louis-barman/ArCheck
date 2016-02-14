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

        projectResults.clear();

        String reportDir = configFile.getReportDir();

        if (reportDir != null) {
            Outcome outcome = setHtmlOutput(reportDir);
            if (outcome.failed()) {
                return false;
            }
        }

        try {

            for (ModuleDetails module : projectDetails.getModuleList()) {
                projectResults.createModuleResults(module);
                boolean result = findFilesPhase(module);
                if (!result) {
                    return false;
                }
            }
        } catch (AbortScanException e) {
            return false;

        }
        projectDetails.analysePhase();

        return resultsPhase();
    }

    private boolean findFilesPhase(ModuleDetails module) throws AbortScanException {
        WildcardFileFilter fileFilter = new WildcardFileFilter(configFile.getFileTypes());

        Collection<String> sourceDirs = module.getSourceDirs();

        for (String sourceDir : sourceDirs) {

            if (!passFilesForOneModule(module, sourceDir, fileFilter)) {
                return false;
            }
        }
        return true;
    }

    private boolean passFilesForOneModule(ModuleDetails module, String sourceDir, WildcardFileFilter fileFilter) throws AbortScanException {
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

        Collection<File> files = FileUtils.listFiles(rootDir, fileFilter, TrueFileFilter.INSTANCE);
        FileTreeScanner examineFileTree = new FileTreeScanner(module.getGroupControl());

        for (File file : files) {
            examineFileTree.addSourceFile(file);
        }

        examineFileTree.analyse();

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
            XLog.println("archeck: Generated HTML report. See: file://" + configFile.getReportDirAbsolutePath() + "/index.html");
        }

        ReportGenerator reports = ReportGeneratorFactory.outputGenerator(reportType, outputWrapper);
        return reports.generateReports(projectResults);
    }

    private Outcome setHtmlOutput(String outputDir) {
        reportType = REPORT_HTML;
        return outputWrapper.setOutputDirectory(outputDir);
    }
}
