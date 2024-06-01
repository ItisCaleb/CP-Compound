package com.itiscaleb.cpcompound.component;

import java.time.LocalDateTime;

public class TemplateItem {
    private String fileName;
    private String extension;
    private String fileContent;
    private int lineCount;
    private String lastModified;

    public TemplateItem(String fileName, String extension, String fileContent, String lastModified) {
        this.fileName = fileName;
        this.extension = extension;
        this.fileContent = fileContent;
        this.lineCount = fileContent.split("\n").length;
        this.lastModified = lastModified;
    }

    // Getters
    public String getFileName() { return fileName; }
    public String getExtension() { return extension; }
    public String getFileContent() { return fileContent; }
    public int getLineCount() { return lineCount; }
    public String getLastModified() { return lastModified; }

    // Setters
}
