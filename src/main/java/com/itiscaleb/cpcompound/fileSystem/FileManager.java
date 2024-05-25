package com.itiscaleb.cpcompound.fileSystem;
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
        System.out.println("Attempting to load resource: " + filePath);
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }
    public static void writeTextFile(Path filePath, String content) {
        System.out.println("Attempting to write resource: " + filePath);
        try (BufferedWriter writer = Files.newBufferedWriter(
                filePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeTextFile(String filePath, String content) {
        writeTextFile(Paths.get(filePath), content);
    }
}
