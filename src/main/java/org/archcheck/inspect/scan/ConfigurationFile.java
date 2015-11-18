package org.archcheck.inspect.scan;

import org.archcheck.inspect.model.ModuleDetails;
import org.archcheck.inspect.model.ProjectDetails;
import org.archcheck.inspect.options.Options;
import org.archcheck.inspect.util.Outcome;
import org.archcheck.inspect.util.XLog;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2015 Louis Barman.
 */
@Singleton
public class ConfigurationFile {
    private static final String CONFIG_FIELD_NAME = "name";
    private static final String CONFIG_FIELD_PATH = "path";
    private static final String CONFIG_FIELD_NODES = "max-nodes";
    private static final String CONFIG_ROOT_DIR = "root-dir";
    private static final String CONFIG_FIELD_MAX_DEPTH = "max-depth";
    private static final String CONFIG_HIDE_CLASS = "hide-class";
    private static final String CONFIG_HIDE_IMPORT = "hide-import";
    private static final String CONFIG_MODULES = "modules";
    private static final String CONFIG_REPORT_DIR = "report-dir";
    private final ObjectMapper jsonMapper;
    private final ProjectDetails projectDetails;
    private String rootDirectory = "./";
    private boolean configFileUsed;
    private String reportDir;

    @Inject
    public ConfigurationFile(ProjectDetails projectDetails) {
        this.projectDetails = projectDetails;

        jsonMapper = createJsonMapper();
    }

    private ObjectMapper createJsonMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        return objectMapper;
    }

    public String getProjectRootDir() {
        return rootDirectory;
    }

    public String[] getFileTypes() {
        String[] fileTypes = {"*.java", "*.scala"};

        return fileTypes;
    }


    public Outcome setConfigFile(String configFile) {
        configFileUsed = true;
        XLog.d("setConfigFile " + configFile);

        InputStream input = null;
        Outcome outcome = Outcome.success();

        try {
            File file = new File(configFile);
            input = new FileInputStream(file);

            JsonNode root = jsonMapper.readTree(input);

            String configPath = file.getParent() + "/";
            outcome = passProperties(root, configPath);

        } catch (FileNotFoundException e) {
            return Outcome.failure("Config file not found '" + configFile + "'");
        } catch (IOException e) {
            return Outcome.failure("Invalid config '" + configFile + "' - " + e.getMessage());
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                return Outcome.failure(e);
            }
        }

        return outcome;
    }

    private Outcome passProperties(JsonNode root, String configPath) {

        Outcome outcome = configRootDir(root, configPath);
        if (outcome.failed())  {
            return outcome;
        }

        if (root.has(CONFIG_REPORT_DIR)) {
            String configReportDir = root.get(CONFIG_REPORT_DIR).getValueAsText();
            setReportDir(configReportDir);
        }

        JsonNode jsonArray = root.get(CONFIG_MODULES);
        if (!jsonArray.isArray()) {
            return Outcome.failure("missing modules form the configuration file" );
        }

        int size = jsonArray.size();

        for (int i = 0; i < size; i++) {
            ModuleDetails module = projectDetails.createNewModuleItem();
            Options moduleOptions = module.getOptions();
            JsonNode item = jsonArray.get(i);
            String name = item.get(CONFIG_FIELD_NAME).getTextValue();
            module.setName(name);
            if (item.has(CONFIG_FIELD_MAX_DEPTH)) {
                int maxDepth = item.get(CONFIG_FIELD_MAX_DEPTH).getIntValue();
                moduleOptions.setMaxDepth(maxDepth);
            }

            if (item.has(CONFIG_FIELD_PATH)) {
                module.addPaths(getStringArrayOption(item, CONFIG_FIELD_PATH));
            }

            if (item.has(CONFIG_HIDE_IMPORT)) {
                moduleOptions.addHiddenImports(getStringArrayOption(item, CONFIG_HIDE_IMPORT));
            }
            if (item.has(CONFIG_HIDE_CLASS)) {
                moduleOptions.addHiddenClasses(getStringArrayOption(item, CONFIG_HIDE_CLASS));
            }

        }
        return Outcome.success();
    }

    private Outcome configRootDir(JsonNode root, String configPath) {
        if (!root.has(CONFIG_ROOT_DIR)) {
            return Outcome.success();
        }
        String configRootDir= root.get(CONFIG_ROOT_DIR).getValueAsText();
        rootDirectory = configRootDir;
        if (!rootDirectory.endsWith("/")) {
            rootDirectory += "/";
        }

        if (!rootDirectory.startsWith("/")) {
            rootDirectory = configPath + rootDirectory;
        }
        Outcome outcome = validateDirectory(rootDirectory, configRootDir);

        return outcome;
    }

    private Collection<String> getStringArrayOption(JsonNode item, String filedName ) {
        Collection<String> result = new ArrayList<String>();
        JsonNode jsonStrings =  item.get(filedName);
        if (jsonStrings.isTextual()) {
            result.add(jsonStrings.getTextValue());
        } else  if (jsonStrings.isArray()) {
            for(JsonNode jsonString : jsonStrings )
                result.add(jsonString.getTextValue());
        }
        return result;
    }

    private Outcome validateDirectory(String directoryPath, String configPath) {
        File file = new File(directoryPath);
        if (!file.isDirectory()) {
            return Outcome.failure("not a valid directory: " + configPath);
        }
        return Outcome.success();
    }

    public void addPathToSource(String pathToSource) {
        pathToSource = stripTrailingSlashes(pathToSource);

        rootDirectory = pathToSource;
        ModuleDetails module = projectDetails.createNewModuleItem();
        module.addPath(".");
        pathToSource = stripTrailingSlashes(pathToSource);
        if (pathToSource.length() == 0) {
            return;
        }

        int index = pathToSource.lastIndexOf("/");
        if (index >= 0) {
            pathToSource = pathToSource.substring(index + 1);
        }
        module.setName(pathToSource);
    }

    protected String stripTrailingSlashes(final String pathToSource) {
        String stripped = pathToSource.trim();
        while (stripped.endsWith("/")) {
            int newLength = stripped.length() - 1;
            if (newLength <= 0) {
                return "";
            }
            stripped = stripped.substring(0, newLength);
        }
        return stripped;
    }

    public boolean isConfigFileUsed() {
        return configFileUsed;
    }

    public void setReportDir(String reportDir) {
        this.reportDir = reportDir;
    }

    public String getReportDir() {
        return reportDir;
    }
}
