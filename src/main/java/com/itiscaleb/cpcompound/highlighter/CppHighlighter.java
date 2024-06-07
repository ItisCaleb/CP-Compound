package com.itiscaleb.cpcompound.highlighter;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CppHighlighter extends Highlighter {
    private static final String[] KEYWORDS = new String[] {
          "alignas", "alignof", "and", "and_eq", "asm",
           "bitand", "bitor", "break", "case",
            "catch", "char16_t", "char32_t", "compl",
            "constexpr", "const_cast", "continue", "decltype",
            "default", "delete", "do", "dynamic_cast", "else",
            "explicit", "export", "extern", "for", "goto", "if",
            "mutable", "namespace", "new", "noexcept", "not", "not_eq",
            "operator", "or", "or_eq", "private", "protected", "register", "reinterpret_cast", "return",
            "sizeof", "static_assert", "switch", "synchronized",
            "template", "this", "thread_local", "throw", "try", "typedef",
            "typeid", "typename", "using", "volatile", "wchar_t", "while", "xor", "xor_eq"
    };

    private static final String[] TYPE_KEYWORDS = new String[] {
            "int", "double", "float", "char", "short", "auto",
            "unsigned", "signed", "long", "bool", "void", "union", "nullptr",
            "struct", "class", "enum", "true", "false"
    };

    private static final String[] MODIFIER_KEYWORDS = new String[] {
            "const", "inline", "static", "private", "public", "friend", "virtual"
    };

    private static final String[] PREPROCESSOR_KEYWORDS = new String[] {
            "if", "elif", "else", "endif", "ifdef", "ifndef",
            "define", "undef", "include", "line",
            "error", "pragma", "defined", "__has_include"
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

    public CppHighlighter(){
        this.pattern = PATTERN;
        this.group = GROUP;
    }

}
