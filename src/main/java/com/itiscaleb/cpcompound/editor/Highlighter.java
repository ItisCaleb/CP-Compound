package com.itiscaleb.cpcompound.editor;

import javafx.util.Pair;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Highlighter {
    Pattern pattern;
    List<Pair<String, String>> group;

    public StyleSpans<Collection<String>> computeHighlighting(String text) {
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        if(pattern == null) return spansBuilder.create();
        Matcher matcher = pattern.matcher(text);
        while(matcher.find()) {
            String styleClass = null;
            for (Pair<String, String> pair : group) {
                if(matcher.group(pair.getKey()) != null) {
                    styleClass = pair.getValue();
                }
            }
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

}
