package com.itiscaleb.cpcompound.editor;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CHighlighter extends Highlighter {
    private static final String[] KEYWORDS = new String[] {
          "alignas", "alignof", "auto", "break", "case",
            "char", "const", "continue", "default", "do",
            "double", "else", "enum", "extern", "float",
            "for", "goto", "if" ,"inline" ,"int", "long",
            "register", "restrict", "return", "short", "signed",
            "sizeof", "static",  "struct", "switch", "typedef",
            "union", "unsigned", "void", "volatile",
            "_Alignas", "_Alignof", "_Atomic", "_BitInt", "_Bool",
            "_Complex", "_Generic", "_Imaginary", "_Noreturn",
            "_Static_assert", "_Thread_local"
    };

    private static final String[] PREPROCESSOR_KEYWORDS = new String[] {
            "if", "elif", "else", "endif", "ifdef", "ifndef",
            "define", "undef", "include", "line",
            "error", "pragma", "defined"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PREPROCESSOR_KEYWORD_PATTERN = "#\\s*(" + String.join("|", PREPROCESSOR_KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/"   // for whole text processing (text blocks)
            + "|" + "/\\*[^\\v]*" + "|" + "^\\h*\\*([^\\v]*|/)";  // for visible paragraph processing (line by line)

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PREPROCESSOR>" + PREPROCESSOR_KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );
    private static final List<Pair<String,String>> GROUP = new ArrayList<>(
            Arrays.asList(
                    new Pair<>("KEYWORD", "cpp-keyword"),
                    new Pair<>("PREPROCESSOR", "cpp-preprocessor-keyword"),
                    new Pair<>("PAREN", "cpp-paren"),
                    new Pair<>("BRACE", "cpp-brace"),
                    new Pair<>("BRACKET", "cpp-bracket"),
                    new Pair<>("SEMICOLON", "cpp-semicolon"),
                    new Pair<>("STRING", "cpp-string"),
                    new Pair<>("COMMENT", "cpp-comment"))
    );

    CHighlighter(){
        this.pattern = PATTERN;
        this.group = GROUP;
    }

}
