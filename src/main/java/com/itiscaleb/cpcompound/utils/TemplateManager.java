package com.itiscaleb.cpcompound.utils;
import com.itiscaleb.cpcompound.component.TemplateItem;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class TemplateManager {
    private Map<String, ArrayList<TemplateItem>> templates = new HashMap<>();

    public TemplateManager() {
        System.out.println("Creating TemplateManager......");
        build();
    }

    private void build() {
        Path templatesFolder = APPData.resolve("Code Template");
        try {
            if (!Files.exists(templatesFolder)) {
                Files.createDirectory(templatesFolder);
            }
            Files.walkFileTree(templatesFolder, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (attrs.isRegularFile()) {
                        Path parent = file.getParent();
                        String folderName = parent.getFileName().toString();
                        templates.computeIfAbsent(folderName, k -> new ArrayList<>());
                        TemplateItem item = new TemplateItem(file.getFileName().toString(),
                                getFileExtension(file),
                                Files.readString(file),
                                formatRelativeTime(LocalDateTime.ofInstant(attrs.lastModifiedTime().toInstant(), ZoneId.systemDefault()))
                        );
                        templates.get(folderName).add(item);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex == -1 ? "" : fileName.substring(dotIndex + 1);
    }

    public Map<String, ArrayList<TemplateItem>> getTemplates() {
        return templates;
    }
    private String formatRelativeTime(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        long days = ChronoUnit.DAYS.between(dateTime, now);
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);

        if (days > 1) return days + " days ago";
        else if(days == 1) return days + " day ago";
        else if (hours > 0) return hours + " hours ago";
        else if (minutes > 1) return "minutes ago";
        else return "just modified";
    }
    public void displayTemplates() {
        if (templates.isEmpty()) {
            System.out.println("No templates available.");
        } else {
            for (Map.Entry<String, ArrayList<TemplateItem>> entry : templates.entrySet()) {
                System.out.println("Folder: " + entry.getKey());
                for (TemplateItem item : entry.getValue()) {
                    System.out.println("  - " + item.getFileName());
                }
            }
        }
    }

}
