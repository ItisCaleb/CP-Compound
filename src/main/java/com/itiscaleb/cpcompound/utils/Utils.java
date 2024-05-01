package com.itiscaleb.cpcompound.utils;

import com.itiscaleb.cpcompound.CPCompound;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Utils {
    public static String unzipFolder(Path source, String target) throws IOException {

        CPCompound.getLogger().info("Unzipping " + source + " to " + target);
        Path dest = Paths.get(target);
        if(!Files.exists(source)) {
            Files.createDirectories(dest);
        }
        String root;
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()))) {

            // list files in zip
            ZipEntry zipEntry = zis.getNextEntry();
            root = Paths.get(zipEntry.getName()).getParent().toString();
            while (zipEntry != null) {
                Path path = dest.resolve(zipEntry.getName());
                boolean isDirectory = zipEntry.getName().endsWith(File.separator);

                if (isDirectory) {
                    Files.createDirectories(path);
                } else {

                    if (path.getParent() != null) {
                        if (Files.notExists(path.getParent())) {
                            Files.createDirectories(path.getParent());
                        }
                    }

                    // copy files, nio
                    Files.copy(zis, path, StandardCopyOption.REPLACE_EXISTING);
                }

                zipEntry = zis.getNextEntry();

            }
            zis.closeEntry();
        }
        return root;
    }
}
