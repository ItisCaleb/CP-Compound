package com.itiscaleb.cpcompound.fileSystem;
import com.itiscaleb.cpcompound.CPCompound;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileManager {
    public static String readTextFile(String filePath) {
        StringBuilder content = new StringBuilder();
        CPCompound.getLogger().info("Attempting to load resource: " + filePath);
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            CPCompound.getLogger().error(e);
        }
        return content.toString();
    }
    public static void writeTextFile(Path filePath, String content) {
        CPCompound.getLogger().info("Attempting to write resource: {}", filePath);
        try (BufferedWriter writer = Files.newBufferedWriter(
                filePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(content);
        } catch (IOException e) {
            CPCompound.getLogger().error("Error occurred", e);
        }
    }
    public static void writeTextFile(String filePath, String content) {
        writeTextFile(Paths.get(filePath), content);
    }
}
