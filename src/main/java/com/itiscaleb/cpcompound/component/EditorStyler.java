package com.itiscaleb.cpcompound.component;

import javafx.application.Platform;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.StyledTextArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class EditorStyler {
    @SafeVarargs
    static StyleSpans<Collection<String>> merge(int length, StyleSpans<Collection<String>>... spans) {
        var result = new StyleSpansBuilder<Collection<String>>()
                .add(Collections.emptyList(), length)
                .create();
        for (StyleSpans<Collection<String>> s : spans) {
            if(s == null) continue;
            result = result.overlay(s,(a,b)->{
                var arr = new ArrayList<String>();
                arr.addAll(a);
                arr.addAll(b);
                return arr;
            });
        }
        return result;
    }

    @SafeVarargs
    public static void asyncSetSpans(StyleClassedTextArea area, StyleSpans<Collection<String>>... spans){
        CompletableFuture.runAsync(()->{
            var result = merge(area.getLength(), spans);
            Platform.runLater(()->area.setStyleSpans(0, result));
        });
    }
}
