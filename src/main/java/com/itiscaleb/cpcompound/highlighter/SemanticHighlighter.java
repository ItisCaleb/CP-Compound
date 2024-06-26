package com.itiscaleb.cpcompound.highlighter;

import com.itiscaleb.cpcompound.langServer.SemanticToken;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SemanticHighlighter {
    public static StyleSpans<Collection<String>> computeHighlighting(StyleClassedTextArea area, List<SemanticToken> tokens) {
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        System.out.println(tokens);
        if(tokens == null) {
            spansBuilder.add(Collections.emptyList(), area.getLength());
            return spansBuilder.create();
        }
        for (SemanticToken token: tokens) {
            int from = area.getAbsolutePosition(token.getLine(), token.getStart());
            int to = area.getAbsolutePosition(token.getLine(), token.getEnd());
            spansBuilder.add(Collections.emptyList(), from - lastKwEnd);
            Collection<String> collection = fromEnum(token);
            spansBuilder.add(collection, to - from);
            lastKwEnd = to;
        }
        spansBuilder.add(Collections.emptyList(), area.getLength() - lastKwEnd);
        return spansBuilder.create();
    }

    private static Collection<String> fromEnum(SemanticToken token) {
        Collection<String> collection;
        switch (token.getType()){
            case Function -> collection = Collections.singleton("semantic-function");
            case Variable -> collection = Collections.singleton("semantic-variable");
            case Namespace -> collection = Collections.singleton("semantic-namespace");
            case Class -> collection = Collections.singleton("semantic-class");
            case Struct -> collection = Collections.singleton("semantic-struct");
            case Enum -> collection = Collections.singleton("semantic-enum");
            case Operator -> collection = Collections.singleton("semantic-operator");
            case Property -> collection = Collections.singleton("semantic-property");
            default -> collection = Collections.emptyList();
        }
        return collection;
    }
}
