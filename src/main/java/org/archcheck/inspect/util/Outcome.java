package org.archcheck.inspect.util;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class Outcome {
    private final boolean success;
    private String message = "";

    public Outcome(boolean result) {
        success = result;

    }

    public Outcome(boolean result, String message) {
        success = result;
        this.message = message;
    }

    public static Outcome success() {
        return new Outcome(true);

    }

    public static Outcome failure(String message) {
        return new Outcome(false, message);

    }

    public boolean successful() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public static Outcome failure(Exception e) {
        return Outcome.failure(e.getMessage());
    }

    public boolean failed() {
        return !success;
    }
}
