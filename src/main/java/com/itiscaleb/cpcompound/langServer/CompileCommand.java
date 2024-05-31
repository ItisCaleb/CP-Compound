package com.itiscaleb.cpcompound.langServer;

import com.itiscaleb.cpcompound.CPCompound;
import com.itiscaleb.cpcompound.editor.EditorContext;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CompileCommand {
    public String directory;
    public List<String> arguments;
    public String file;

    public static CompileCommand fromContext(EditorContext context){
        CompileCommand cmd = new CompileCommand();
        Path path = context.getPath();
        cmd.directory = path.getParent().toString();
        cmd.file = path.getFileName().toString();
        cmd.arguments = new ArrayList<>();
        switch (context.getLang()){
            case C -> cmd.arguments.add(CPCompound.getConfig().getGCCExe());
            case CPP -> cmd.arguments.add(CPCompound.getConfig().getGPPExe());
        }
        cmd.arguments.add(path.toString());
        return cmd;
    }
}
