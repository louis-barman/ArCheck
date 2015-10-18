package org.archcheck.inspect.app;

import org.archcheck.inspect.scan.ConfigurationFile;
import org.archcheck.inspect.scan.Transverse;
import org.archcheck.inspect.util.Outcome;
import org.archcheck.inspect.util.XLog;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class Director {
    public static final String OPTION_CONFIG = "config";
    private static final String OPTION_HTML = "html";
    private static final String OPTION_HELP = "help";


    @Inject
    Transverse transverse;

    @Inject
    ConfigurationFile configFile;

    private boolean secondArgConsumed;

    public boolean start(String[] args) {
        if (args.length == 0) {
            XLog.printlnError("ERROR: missing <path-to-source-code>");
            XLog.printlnError("USAGE: archcheck [-options] <path-to-source-code>");
            XLog.printlnError("For help use '-?' or  '--help'");
            return true;
        }
        boolean success = passArguments(args);

        if (success) {
            return transverse.start();
        }
        return success;
    }

    private boolean passArguments(String[] args) {
        int argumentIndex = 0;

        while (argumentIndex < args.length) {
            String firstArg = args[argumentIndex];
            String secondArg = (argumentIndex + 1 < args.length) ? args[argumentIndex + 1] : "";

            if (firstArg.length() >= 2 && firstArg.charAt(0) == '-') {
                boolean result = passSingleOption(firstArg, secondArg);
                if (!result) {
                    return false;
                }
            } else {
                if (!secondArgConsumed) {
                    configFile.addPathToSource(firstArg);
                }
            }
            if (secondArgConsumed) {
                argumentIndex++;
                secondArgConsumed = false;
            }

            argumentIndex++;
        }
        return true;
    }

    private boolean passSingleOption(String firstArg, String secondArg) {
        Outcome outcome = Outcome.success();

        if (matchOption(firstArg, '?', OPTION_HELP)) {
            printHelp();
        } else if (matchOption(firstArg, 'c', OPTION_CONFIG)) {
            if (secondArg.length() == 0) {
                return errorMissingOption(OPTION_CONFIG);
            }
            outcome = configFile.setConfigFile(secondArg);
            consumeSecondArg();
        } else if (matchOption(firstArg, 'c', OPTION_HTML)) {
            if (secondArg.length() == 0) {
                return errorMissingOption(OPTION_HTML);
            }
            outcome = transverse.setHtmlOutput(secondArg);
            consumeSecondArg();
        } else {
            XLog.error("Unrecognised option: " + firstArg);
            return false;
        }
        if (!outcome.successful()) {
            XLog.error(outcome.getMessage());
            return false;
        }

        return true;
    }

    private void consumeSecondArg() {
        secondArgConsumed = true;
    }

    private boolean errorMissingOption(String option) {
        XLog.error("-" + option + " parameter is missing");
        return false;
    }

    private boolean matchOption(String firstArg, char c, String config) {
        if (firstArg.charAt(1) == '-') {
            if (config.equals(firstArg.substring(2, firstArg.length()))) {
                return true;
            }

        } else if (firstArg.charAt(1) == c) {
            return true;
        }

        return false;
    }

    protected void printHelp() {
        InputStreamReader irs = new InputStreamReader(Director.class.getResourceAsStream("/strings/help-string.txt"), Charset.forName("UTF-8"));
        BufferedReader bufferedReader = new BufferedReader(irs);
        try {
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                XLog.println(line);
            }
        } catch (IOException e) {
            XLog.e(e.getMessage());
        }
    }

}
