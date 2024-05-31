package com.itiscaleb.cpcompound.editor;

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
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\""+ "|" +"'([^'\\\\]|\\\\.)*'";;
    private static final String COMMENT_PATTERN = "#[^\n]*" + "|" + "'''\\*(.|\\R)*?\\*'''"   // for whole text processing (text blocks)
            + "|" + "/\\*[^\\v]*" + "|" + "^\\h*\\*([^\\v]*|/)";  // for visible paragraph processing (line by line)

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );
    private static final List<Pair<String,String>> GROUP = new ArrayList<>(
            Arrays.asList(
                    new Pair<>("KEYWORD", "py-keyword"),
                    new Pair<>("PAREN", "py-paren"),
                    new Pair<>("BRACE", "py-brace"),
                    new Pair<>("BRACKET", "py-bracket"),
                    new Pair<>("STRING", "py-string"),
                    new Pair<>("COMMENT", "py-comment"))
    );

    PythonHighlighter(){
        this.pattern = PATTERN;
        this.group = GROUP;
    }

}
