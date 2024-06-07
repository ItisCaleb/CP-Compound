package com.itiscaleb.cpcompound.highlighter;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class JSONHighlighter extends Highlighter {

    private static final String NUMBER_PATTERN = Highlighter.NUMBER_PATTERN + "(?:[eE][+-]?\\d+)?[fF]?\\b"
            + "|" + "\\b-?0x[\\da-fA-F]+\\b" + "|" + "\\b-?0b[01]+\\b";

    private static final Pattern PATTERN = Pattern.compile(
            "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<NUMBER>" + NUMBER_PATTERN + ")"

    );
    private static final List<Pair<String,String>> GROUP = new ArrayList<>(
            Arrays.asList(
                    new Pair<>("BRACE", "brace"),
                    new Pair<>("BRACKET", "bracket"),
                    new Pair<>("STRING", "string"),
                    new Pair<>("COMMENT", "comment"),
                    new Pair<>("NUMBER", "number"))
    );

    public JSONHighlighter(){
        this.pattern = PATTERN;
        this.group = GROUP;
    }

}
