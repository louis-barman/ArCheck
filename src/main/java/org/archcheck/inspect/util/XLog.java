package org.archcheck.inspect.util;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class XLog {

    public static void d(String message) {
        //System.out.println("DEBUG: " + message);
    }

    public static void v(String message) {
        //System.out.println("VERBOSE: " + message);
    }

    public static void w(String message) {
        System.err.println("WARNING: " + message);
    }

    public static void e(String message) {
        System.err.println("ERROR: " + message);
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

    public static void trace(String message) {
        System.out.println("TRACE: " + message);
    }
}
