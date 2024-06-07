package com.itiscaleb.cpcompound.highlighter;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class NoneHighlighter extends Highlighter{
    public NoneHighlighter(){
        this.pattern = Pattern.compile("");
        this.group = new ArrayList<>();
    }
}
