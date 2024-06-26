package com.itiscaleb.cpcompound.highlighter;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class PythonHighlighter extends Highlighter {
    private static final String[] KEYWORDS = new String[] {
          "and", "as", "assert", "break", "class", "continue",
            "def", "del", "elif", "else", "except", "False",
            "finally", "for", "from", "global", "if", "import",
            "in", "is", "lambda", "None", "nonlocal", "not", "or",
            "pass", "raise", "return", "True", "try", "while", "with",
            "yield"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String STRING_PATTERN = Highlighter.STRING_PATTERN + "|" +"'([^'\\\\]|\\\\.)*'";;
    private static final String COMMENT_PATTERN = "#[^\n]*" + "|" + "'''(.|\\R)*?'''"   // for whole text processing (text blocks)
            + "|" + "\"\"\"(.|\\R)*?\"\"\"";  // for visible paragraph processing (line by line)
    private static final String NUMBER_PATTERN = Highlighter.NUMBER_PATTERN + "\\b|" +
            "\\b-?0x[\\da-fA-F]+\\b" + "|" + "\\b-?0b[01]+\\b" + "|" + "\\b-?0o\\d+\\b";;

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
    );
    private static final List<Pair<String,String>> GROUP = new ArrayList<>(
            Arrays.asList(
                    new Pair<>("KEYWORD", "py-keyword"),
                    new Pair<>("PAREN", "paren"),
                    new Pair<>("BRACE", "brace"),
                    new Pair<>("BRACKET", "bracket"),
                    new Pair<>("STRING", "string"),
                    new Pair<>("COMMENT", "comment"),
                    new Pair<>("NUMBER", "number"))
    );

    public PythonHighlighter(){
        this.pattern = PATTERN;
        this.group = GROUP;
    }

}
