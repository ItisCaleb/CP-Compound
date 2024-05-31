package com.itiscaleb.cpcompound.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class SysInfo {
    static OS os = null;
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
        if(os != null){
            return os;
        }
        String name = System.getProperty("os.name").toLowerCase();
        if(name.contains("win")) os = OS.WIN;
        else if(name.contains("darwin") || name.contains("mac")) os = OS.MAC;
        else if(name.contains("linux")) os = OS.LINUX;
        else throw new RuntimeException("Unsupported OS: " + os);
        return os;
    }

    public static String getArch() throws IOException {{
            if (SysInfo.getOS() == OS.WIN) {
                return System.getProperty("os.arch");
            } else {
                String name = null;
                ProcessBuilder builder = new ProcessBuilder("uname", "-m");
                builder.redirectError(ProcessBuilder.Redirect.INHERIT);
                Process p = builder.start();
                try (BufferedReader output = new BufferedReader(
                        new InputStreamReader(p.getInputStream(), Charset.defaultCharset()))) {

                    String line;
                    while ((line = output.readLine()) != null) {
                        line = line.trim();
                        if (!line.isEmpty()) {
                            name = line;
                        }
                    }
                }
                return name;
            }




        }
    }
}
