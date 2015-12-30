package org.archcheck.inspect.model;

/**
 * Created by louisbarman on 20/11/2015.
 */
public class SourceStatistics {
    private final String sourcePath;
    private final long fileSize;

    public SourceStatistics(String sourcePath, long fileSize) {
        this.sourcePath = sourcePath;
        this.fileSize = fileSize;
    }

    public long getFileSize() {
        return fileSize;
    }
}
