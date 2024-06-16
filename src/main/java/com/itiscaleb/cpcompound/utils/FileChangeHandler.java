package com.itiscaleb.cpcompound.utils;


import java.nio.file.Path;

public interface FileChangeHandler {
    void onFileCreated(Path path);
    void onFileDeleted(Path path);
    void onFileModified(Path path);
}
