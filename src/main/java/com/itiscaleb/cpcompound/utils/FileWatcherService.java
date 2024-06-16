package com.itiscaleb.cpcompound.utils;
import javafx.application.Platform;
import javafx.scene.control.TreeView;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;

import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileWatcherService {
    private WatchService watchService;
    private List<FileChangeHandler> handlers = new ArrayList<>();
    private Path watchedDirectory;
    public FileWatcherService(Path pathToWatch) throws IOException {
        this.watchedDirectory = pathToWatch;
        watchService = FileSystems.getDefault().newWatchService();
        registerDirectoryAndSubdirectories(pathToWatch);
        startWatching();
    }
    private void registerDirectoryAndSubdirectories(Path startDir) throws IOException {
        Files.walkFileTree(startDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    public void registerHandler(FileChangeHandler handler) {
        handlers.add(handler);
    }

    public void unregisterHandler(FileChangeHandler handler) {
        handlers.remove(handler);
    }

    private void startWatching() {
        Thread thread = new Thread(() -> {
            try {
                WatchKey key;
                while ((key = watchService.take()) != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        notifyHandlers(event, (Path) key.watchable());
                    }
                    if (!key.reset()) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        thread.start();
    }

    private void notifyHandlers(WatchEvent<?> event, Path watchedPath) {
        Path affectedRelativePath = (Path) event.context();
        Path affectedAbsolutePath = watchedPath.resolve(affectedRelativePath);
        if (event.kind() == ENTRY_CREATE && Files.isDirectory(affectedAbsolutePath)) {
            try {
                registerDirectoryAndSubdirectories(affectedAbsolutePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (FileChangeHandler handler : handlers) {
            if (event.kind() == ENTRY_CREATE) {
                handler.onFileCreated(affectedAbsolutePath);
            } else if (event.kind() == ENTRY_DELETE) {
                handler.onFileDeleted(affectedAbsolutePath);
            } else if (event.kind() == ENTRY_MODIFY) {
                handler.onFileModified(affectedAbsolutePath);
            }
        }
    }
}
