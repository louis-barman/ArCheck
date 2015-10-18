package org.archcheck.inspect.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright (C) 2015 Louis Barman.
 */
public class ResultsList implements Iterable<ResultsHolder> {

    List resultsList = new ArrayList();

    public void add(Object object) {
        resultsList.add(object);
    }

    public int size() {
        return resultsList.size();
    }

    public Object get(int index) {
        return resultsList.get(index);
    }

    public <T extends Object> T get(int index, Class<T> type) {
        return type.cast(resultsList.get(index));
    }

    public ResultsList getResultsList(int index) {
        return (ResultsList) resultsList.get(index);
    }

    public String getString(int index) {
        return (String) resultsList.get(index);
    }

    public ResultsHolder getResultsHolder(int index) {
        return (ResultsHolder) resultsList.get(index);
    }

    @Override
    public Iterator<ResultsHolder> iterator() {
        return resultsList.iterator();
    }
}
