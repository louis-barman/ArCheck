package org.archcheck.inspect.report;

import org.archcheck.inspect.model.ResultsHolder;
import org.archcheck.inspect.model.ResultsList;
import org.archcheck.inspect.results.ModuleResults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by louisbarman on 01/11/15.
 */
public class ReportLimits {


    private static final int MAX_BLOBBLES = 12; // or Nodes on the diagram

    public List<String> getComponentTrimList(ModuleResults moduleResults) {
        ArrayList<String> trimList = new ArrayList<String>();
        ResultsList table = moduleResults.getCodeGroupSummaryTable();

        if (MAX_BLOBBLES == 0 || table.size() <= MAX_BLOBBLES)  {
            return trimList;
        }

        ArrayList<LimitItem> allItems = new ArrayList<LimitItem>();
        for (ResultsHolder row : table) {
            allItems.add(new LimitItem(row.getString("fullName"), row.getLong("fileSize")));
        }

        Collections.sort(allItems);

        int size = allItems.size() - MAX_BLOBBLES ;
        if (size < 0) {
            size = 0;
        }

        for (int i = 0; i < size; i++) {
            trimList.add(allItems.get(i).getFullName());
        }

        return trimList;
    }

    private class LimitItem implements Comparable<LimitItem> {

        private final String fullName;
        private final long fileSize;

        public LimitItem(String fullName, long fileSize) {

            this.fullName = fullName;
            this.fileSize = fileSize;
        }

        @Override
        public int compareTo(LimitItem o) {
             return (Long.compare(fileSize, o.fileSize));
        }

        public String getFullName() {
            return fullName;
        }
    }
}
