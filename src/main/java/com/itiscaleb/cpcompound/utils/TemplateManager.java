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
    private List<String> order = new ArrayList<>();
    private Path orderFilePath;
    public TemplateManager() {
        System.out.println("Creating TemplateManager......");
        orderFilePath = APPData.resolve(".config/order");
        build();
    }
    private void createConfigDirectory() {
        try {
            Path configDir = APPData.resolve(".config");
            if (!Files.exists(configDir)) {
                Files.createDirectory(configDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void build() {
        createConfigDirectory();
        loadOrder();
        Path templatesFolder = APPData.resolve("Code Template");
        try {
            if (!Files.exists(templatesFolder)) {
                Files.createDirectory(templatesFolder);
            }
            Files.walkFileTree(templatesFolder, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    String folderName = dir.getFileName().toString();
                    if(folderName.equals("Code Template")){
                        return FileVisitResult.CONTINUE;
                    }
                    // Ensure every directory has an entry, even if it's empty
                    templates.putIfAbsent(folderName, new ArrayList<>());
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (attrs.isRegularFile()) {
                        Path parent = file.getParent();
                        String folderName = parent.getFileName().toString();
//                        System.out.println(folderName+"is regular file");
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

        updateOrder(templates);
    }
    private void loadOrder() {
        try {
            if (Files.exists(orderFilePath)) {
                order = Files.readAllLines(orderFilePath);
            }else{
                Files.createFile(orderFilePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void updateOrder(Map<String, ArrayList<TemplateItem>> curTemplate) {
        //Remove entries from order that are no longer in curTemplate
        order.removeIf(folder -> !curTemplate.containsKey(folder));
        //Append new directories from curTemplate to order if they're not already included
        for (String key : curTemplate.keySet()) {
            if (!order.contains(key)) {
                order.add(key);
            }
        }
        saveOrder();
    }
    private void saveOrder() {
        try {
            Files.write(orderFilePath, order);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void pushBackOrder(String folderName) {
        order.add(folderName);
        saveOrder();
    }
    public void updateOrderByName(String oldName,String newName){
        order.set(order.indexOf(oldName),newName );
        saveOrder();
    }
    public void removeOrderByName(String Name){
        order.remove(order.indexOf(Name));
        saveOrder();
    }
    public Map<String, ArrayList<TemplateItem>> getTemplatesByOrder() {
        LinkedHashMap<String, ArrayList<TemplateItem>> orderedTemplates = new LinkedHashMap<>();
        for (String key : order) {
            ArrayList<TemplateItem> items = templates.get(key);
            if (items != null) {
                orderedTemplates.put(key, items);
            }
        }
        return orderedTemplates;
    }
    public String getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex == -1 ? "" : fileName.substring(dotIndex + 1);
    }

    public Map<String, ArrayList<TemplateItem>> getTemplates() {
        return templates;
    }
    public String formatRelativeTime(LocalDateTime dateTime) {
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
