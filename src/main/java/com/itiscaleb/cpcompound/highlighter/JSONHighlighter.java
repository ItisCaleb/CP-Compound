package com.itiscaleb.cpcompound.highlighter;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class JSONHighlighter extends Highlighter {
    private static final String[] KEYWORDS = new String[] {
      "true", "false", "null",
    };
    private static final String KEYWORD_PATTERN ="\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String KEY_PATTERN = STRING_PATTERN + ":";
    private static final String NUMBER_PATTERN = Highlighter.NUMBER_PATTERN + "(?:[eE][+-]?\\d+)?\\b";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<KEY>" + KEY_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<NUMBER>" + NUMBER_PATTERN + ")"

    );
    private static final List<Pair<String,String>> GROUP = new ArrayList<>(
            Arrays.asList(
                    new Pair<>("KEYWORD", "json-keyword"),
                    new Pair<>("BRACE", "brace"),
                    new Pair<>("BRACKET", "bracket"),
                    new Pair<>("KEY", "json-key"),
                    new Pair<>("STRING", "string"),
                    new Pair<>("NUMBER", "number"))
    );

    public JSONHighlighter(){
        this.pattern = PATTERN;
        this.group = GROUP;
    }

}
