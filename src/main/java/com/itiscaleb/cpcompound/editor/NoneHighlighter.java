package com.itiscaleb.cpcompound.editor;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class NoneHighlighter extends Highlighter{
    NoneHighlighter(){
        this.pattern = Pattern.compile("");
        this.group = new ArrayList<>();
    }
}
