package com.itiscaleb.cpcompound.utils;

import com.itiscaleb.cpcompound.CPCompound;
import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Utils {

    // return root folder
    public static String unzipFolder(Path source, String target) throws IOException {

        CPCompound.getLogger().info("Unzipping {} to {}", source, target);
        Path dest = Paths.get(target);
        if(!Files.exists(source)) {
            Files.createDirectories(dest);
        }
        String root;
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()))) {

            // list files in zip
            ZipEntry zipEntry = zis.getNextEntry();
            root = Paths.get(zipEntry.getName()).getName(0).toString();

            while (zipEntry != null) {
                Path path = dest.resolve(zipEntry.getName());
                if (zipEntry.isDirectory()) {
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

    public static String unzip7z(Path source, String target) throws IOException {
        CPCompound.getLogger().info("Unzipping {} to {}", source, target);
        Path dest = Paths.get(target);
        if(!Files.exists(source)) {
            Files.createDirectories(dest);
        }
        String root;
        try(RandomAccessFile randomAccessFile = new RandomAccessFile(source.toFile(), "r")) {
            var archive = SevenZip.openInArchive(null,
                    new RandomAccessFileInStream(randomAccessFile));
            root = (String) archive.getProperty(0, PropID.PATH);
            int[] items = new int[archive.getNumberOfItems()];
            for (int i = 0; i < items.length; i++) {
                items[i] = i;
            }
            archive.extract(items,
                    false, new SevenZipExtractCallback(archive, dest));
        }
        return root;
    }

    static class SevenZipExtractCallback implements IArchiveExtractCallback {
        private IInArchive inArchive;
        private int index;
        private OutputStream outputStream;
        private File file;
        private boolean isFolder;
        private Path target;

        SevenZipExtractCallback(IInArchive inArchive, Path target) {
            this.inArchive = inArchive;
            this.target = target;
        }

        @Override
        public void setTotal(long total) {

        }

        @Override
        public void setCompleted(long completeValue) {

        }

        @Override
        public ISequentialOutStream getStream(int index,
                                              ExtractAskMode extractAskMode) throws SevenZipException {
            closeOutputStream();

            this.index = index;
            this.isFolder = (Boolean) inArchive.getProperty(index,
                    PropID.IS_FOLDER);

            if (extractAskMode != ExtractAskMode.EXTRACT) {
                // Skipped files or files being tested
                return null;
            }

            String p = (String) inArchive.getProperty(index, PropID.PATH);
            Path path = target.resolve(p);
            try {
                if (isFolder) {
                    Files.createDirectories(path);
                    return null;
                }
                if (path.getParent() != null) {
                    if (Files.notExists(path.getParent())) {
                        Files.createDirectories(path.getParent());
                    }
                }
            }catch (IOException e) {
                CPCompound.getLogger().error("Error occurred", e);
            }
            file = path.toFile();

            try {
                outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                throw new SevenZipException("Error opening file: "
                        + file.getAbsolutePath(), e);
            }

            return data -> {
                try {
                    outputStream.write(data);
                } catch (IOException e) {
                    throw new SevenZipException("Error writing to file: "
                            + file.getAbsolutePath());
                }
                return data.length; // Return amount of consumed data
            };
        }


        private void closeOutputStream() throws SevenZipException {
            if (outputStream != null) {
                try {
                    outputStream.close();
                    outputStream = null;
                } catch (IOException e) {
                    throw new SevenZipException("Error closing file: "
                            + file.getAbsolutePath());
                }
            }
        }

        @Override
        public void prepareOperation(ExtractAskMode extractAskMode) {

        }

        @Override
        public void setOperationResult(
                ExtractOperationResult extractOperationResult)
                throws SevenZipException {
            closeOutputStream();
            String path = (String) inArchive.getProperty(index, PropID.PATH);
            if (extractOperationResult != ExtractOperationResult.OK) {
                throw new SevenZipException("Invalid file: " + path);
            }
        }
    }
}
