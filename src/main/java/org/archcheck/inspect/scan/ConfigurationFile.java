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
import java.util.Iterator;

/**
 * Copyright (C) 2015 Louis Barman.
 */
@Singleton
public class ConfigurationFile {
    public static final String CONFIG_FIELD_NAME = "name";
    public static final String CONFIG_FIELD_PATH = "path";
    public static final String CONFIG_FIELD_MAX_DEPTH = "max-depth";
    public static final String CONFIG_HIDE_IMPORT = "hide-import";
    private final ObjectMapper jsonMapper;
    private final ProjectDetails projectDetails;
    private String rootDirectory = "";

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
            return Outcome.failure(e);
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

        rootDirectory = root.get("root-dir").getValueAsText();
        if (rootDirectory == null) {
            return Outcome.failure("Missing root-dir");
        }
        if (!rootDirectory.endsWith("/")) {
            rootDirectory += "/";
        }

        if (!rootDirectory.startsWith("/")) {
            rootDirectory = configPath + rootDirectory;
        }
        Outcome outcome = validateDirectory(rootDirectory);
        if (outcome.failed())  {
            return outcome;
        }
        JsonNode jsonArray = root.get("modules");
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
            String path = item.get(CONFIG_FIELD_PATH).getTextValue();
            module.setPath(path);
            if (item.has(CONFIG_FIELD_MAX_DEPTH)) {
                int maxDepth = item.get(CONFIG_FIELD_MAX_DEPTH).getIntValue();
                moduleOptions.setMaxDepth(maxDepth);
            }


            findModuleOptions(item, moduleOptions);
        }
        return Outcome.success();
    }

    private Outcome validateDirectory(String directoryPath) {
        File file = new File(directoryPath);
        if (!file.isDirectory()) {
            return Outcome.failure("not a valid directory: " + directoryPath);
        }
        return Outcome.success();
    }

    private void findModuleOptions(JsonNode root, Options moduleOptions) {
        JsonNode node = root.get(CONFIG_HIDE_IMPORT);

        if (node == null) {
            return;
        }

        if (node.isArray()) {
            Iterator<JsonNode> ite = node.getElements();

            while (ite.hasNext()) {
                JsonNode item = ite.next();
                moduleOptions.addHiddenImport(item.getTextValue());

            }
        }
    }

    public void addPathToSource(String pathToSource) {
        pathToSource = stripTrailingSlashes(pathToSource);
        rootDirectory = pathToSource;
        ModuleDetails module = projectDetails.createNewModuleItem();
        module.setPath("");
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

}
