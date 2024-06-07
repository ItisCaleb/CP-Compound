package com.itiscaleb.cpcompound.highlighter;

import com.itiscaleb.cpcompound.langServer.SemanticToken;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DiagnosticHighlighter {

    static int rangeToPosition(StyleClassedTextArea area, org.eclipse.lsp4j.Position p) {
        return area.getAbsolutePosition(p.getLine(), p.getCharacter());
    }
    // Reference:
    // https://github.com/FXMisc/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/SpellCheckingDemo.java
    // for compute diagnostic style
    public static StyleSpans<Collection<String>> computeHighlighting(StyleClassedTextArea area, List<Diagnostic> diagnostics) {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        int last = 0;
        if(diagnostics == null) return null;
        for (Diagnostic diagnostic : diagnostics) {
            // convert line and character to index
            Range range = diagnostic.getRange();
            int from = rangeToPosition(area, range.getStart());
            int to = rangeToPosition(area, range.getEnd());
            int len = from - last;
            if(len < 0) continue;
            spansBuilder.add(Collections.emptyList(), len);
            last = to;
            switch (diagnostic.getSeverity()) {
                case Error:
                    spansBuilder.add(Collections.singleton("underlined-red"), to - from);
                    break;
                case Warning:
                case Information:
                    spansBuilder.add(Collections.singleton("underlined-yellow"), to - from);
                    break;
            }
        }
        spansBuilder.add(Collections.emptyList(), area.getLength() - last);
        return spansBuilder.create();
    }

}
