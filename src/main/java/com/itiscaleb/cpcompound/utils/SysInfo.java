package com.itiscaleb.cpcompound.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class SysInfo {
    public enum OS{
        WIN("windows"),
        LINUX("linux"),
        MAC("mac");
        public final String name;

        OS(String name) {
            this.name = name;
        }
    }
    public static OS getOS() throws RuntimeException{
        String os = System.getProperty("os.name").toLowerCase();
        if(os.contains("win")) return OS.WIN;
        else if(os.contains("darwin") || os.contains("mac")) return OS.MAC;
        else if(os.contains("linux")) return OS.LINUX;
        else throw new RuntimeException("Unsupported OS: " + os);
    }

    public static String getArch() throws IOException, InterruptedException {{
            String name = null;

            ProcessBuilder builder;
            if (SysInfo.getOS() == OS.WIN) {
                builder = new ProcessBuilder("wmic", "os", "get", "OSArchitecture");
            } else {
                builder = new ProcessBuilder("uname", "-m");
            }
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);

            Process process = builder.start();

            try (BufferedReader output = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), Charset.defaultCharset()))) {

                String line;
                while ((line = output.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        name = line;
                    }
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException(
                        "Process " + builder.command() + " returned " + exitCode);
            }

            return name;
        }
    }
}
