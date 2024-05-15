package com.itiscaleb.cpcompound.utils;
import java.io.*;

public class FileManager {
    public static String readTextFile(String filePath) {
        StringBuilder content = new StringBuilder();
        System.out.println("Attempting to load resource: " + filePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}
