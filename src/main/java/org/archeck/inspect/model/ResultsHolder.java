package org.archeck.inspect.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class ResultsHolder {

    private final Map<String, Object> results = new HashMap<String, Object>();


    public void put(String key, Object value) {
        if (results.containsKey(key)) {
            throw new IllegalStateException("This key has already by used \"" + key + "\"");
        }
        results.put(key, value);
    }

    public Object get(String key) {
        Object result = results.get(key);
        if (result == null) {
            throw new IllegalStateException("This key has not be set: \"" + key + "\"");
        }
        return result;
    }

    public <T extends Object> T getTypedResult(String key, Class<T> type) {
        return type.cast(get(key));
    }

    public String getString(String key) {
        return (String) get(key);
    }

    public String getString2(String key) {

        return getTypedResult(key, String.class);
    }

    public ResultsList getResultsList(String key) {
        return (ResultsList) get(key);
    }

    public int getInt(String key) {
        return (Integer) get(key);
    }

    public boolean getBool(String key) {
        return (Boolean) get(key);
    }

    public long getLong(String key)  {
        return (Long) get(key);
    }

}
