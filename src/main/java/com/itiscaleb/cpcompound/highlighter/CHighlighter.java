package com.itiscaleb.cpcompound.highlighter;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CHighlighter extends Highlighter {
    private static final String[] KEYWORDS = new String[] {
          "alignas", "alignof", "auto", "break", "case",
            "continue", "default", "do", "else",
            "extern", "for", "goto", "if", "register", "restrict", "return",
            "sizeof", "switch", "typedef", "volatile",
            "_Alignas", "_Alignof", "_Atomic", "_BitInt", "_Bool",
            "_Complex", "_Generic", "_Imaginary", "_Noreturn",
            "_Static_assert", "_Thread_local"
    };

    private static final String[] TYPE_KEYWORDS = new String[] {
            "int", "double", "float", "char", "short",
            "unsigned", "signed", "long", "void", "union", "NULL",
            "struct", "enum",
    };

    private static final String[] MODIFIER_KEYWORDS = new String[] {
            "const", "inline", "static"
    };

    private static final String[] PREPROCESSOR_KEYWORDS = new String[] {
            "if", "elif", "else", "endif", "ifdef", "ifndef",
            "define", "undef", "include", "line",
            "error", "pragma", "defined"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String TYPE_KEYWORD_PATTERN = "\\b(" + String.join("|", TYPE_KEYWORDS) + ")\\b";
    private static final String MODIFIER_KEYWORD_PATTERN = "\\b(" + String.join("|", MODIFIER_KEYWORDS) + ")\\b";
    private static final String PREPROCESSOR_KEYWORD_PATTERN = "#\\s*(" + String.join("|", PREPROCESSOR_KEYWORDS) + ")\\b";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/"   // for whole text processing (text blocks)
            + "|" + "/\\*[^\\v]*" + "|" + "^\\h*\\*([^\\v]*|/)";  // for visible paragraph processing (line by line)
    private static final String NUMBER_PATTERN = Highlighter.NUMBER_PATTERN + "(?:[eE][+-]?\\d+)?[fF]?\\b"
            + "|" + "\\b-?0x[\\da-fA-F]+\\b" + "|" + "\\b-?0b[01]+\\b";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<TYPE>" + TYPE_KEYWORD_PATTERN + ")"
                    + "|(?<MODIFIER>" + MODIFIER_KEYWORD_PATTERN + ")"
                    + "|(?<PREPROCESSOR>" + PREPROCESSOR_KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<NUMBER>" + NUMBER_PATTERN + ")"

    );
    private static final List<Pair<String,String>> GROUP = new ArrayList<>(
            Arrays.asList(
                    new Pair<>("KEYWORD", "cpp-keyword"),
                    new Pair<>("TYPE", "cpp-type-keyword"),
                    new Pair<>("MODIFIER", "cpp-modifier-keyword"),
                    new Pair<>("PREPROCESSOR", "cpp-preprocessor-keyword"),
                    new Pair<>("PAREN", "paren"),
                    new Pair<>("BRACE", "brace"),
                    new Pair<>("BRACKET", "bracket"),
                    new Pair<>("SEMICOLON", "semicolon"),
                    new Pair<>("STRING", "string"),
                    new Pair<>("COMMENT", "comment"),
                    new Pair<>("NUMBER", "number"))
    );

    public CHighlighter(){
        this.pattern = PATTERN;
        this.group = GROUP;
    }

}
