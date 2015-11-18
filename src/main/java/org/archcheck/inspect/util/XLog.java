package org.archcheck.inspect.util;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class XLog {

    public static void d(String string) {
        //System.out.println("DEBUG: " + string);
    }

    public static void v(String string) {
        //System.out.println("VERBOSE: " + string);
    }

    public static void w(String string) {
        System.err.println("WARNING: " + string);
    }

    public static void e(String string) {
        System.err.println("ERROR: " + string);
    }

    public static void println(String s) {
        System.out.println(s);
    }

    public static void printlnError(String s) {
        System.err.println(s);
    }

    public static void internalError(String message) {
        XLog.e("Internal Error: " + message);

    }

    public static void error(String message) {
        XLog.e(message);
    }

    public static void error(Exception exception) {
        error(exception.getMessage());
    }

}
