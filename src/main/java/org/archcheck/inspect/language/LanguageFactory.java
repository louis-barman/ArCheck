package org.archcheck.inspect.language;

/**
 * Copyright (C) 2015 Louis Barman on 03/08/15.
 */
public class LanguageFactory {
    private LanguageFactory() {

    }

    public static AnyLanguage languageDecoder(String language) {
        return new JavaLanguage();
    }
}
