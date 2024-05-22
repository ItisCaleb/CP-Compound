package com.itiscaleb.cpcompound.editor;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CppHighlighter extends Highlighter {
    private static final String[] KEYWORDS = new String[] {
          "alignas", "alignof", "and", "and_eq", "asm",
           "auto", "bitand", "bitor", "bool", "break", "case",
            "catch", "char", "char16_t", "char32_t", "class", "compl",
            "const", "constexpr", "const_cast", "continue", "decltype",
            "default", "delete", "do", "double", "dynamic_cast", "else",
            "enum", "explicit", "export", "extern", "false", "float",
            "for", "friend", "goto", "if" ,"inline" ,"int", "long",
            "mutable", "namespace", "new", "noexcept", "not", "not_eq",
            "nullptr", "operator", "or", "or_eq", "private", "protected",
            "public", "register", "reinterpret_cast", "return", "short", "signed",
            "sizeof", "static", "static_assert", "struct", "switch", "synchronized",
            "template", "this", "thread_local", "throw", "true", "try", "typedef",
            "typeid", "typename", "union", "unsigned", "using", "virtual", "void",
            "volatile", "wchar_t", "while", "xor", "xor_eq"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/"   // for whole text processing (text blocks)
            + "|" + "/\\*[^\\v]*" + "|" + "^\\h*\\*([^\\v]*|/)";  // for visible paragraph processing (line by line)

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
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
                    new Pair<>("PAREN", "cpp-paren"),
                    new Pair<>("BRACE", "cpp-brace"),
                    new Pair<>("BRACKET", "cpp-bracket"),
                    new Pair<>("SEMICOLON", "cpp-semicolon"),
                    new Pair<>("STRING", "cpp-string"),
                    new Pair<>("COMMENT", "cpp-comment"))
    );

    CppHighlighter(){
        this.pattern = PATTERN;
        this.group = GROUP;
    }

}
