package com.itiscaleb.cpcompound.langServer;

public class SemanticToken {
    int line;
    int start, end;
    SemanticTokenType tokenType;
    int modifier;
    public SemanticToken(int line, int start, int end, SemanticTokenType tokenType, int modifier) {
        this.line = line;
        this.start = start;
        this.end = end;
        this.tokenType = tokenType;
        this.modifier = modifier;
    }

    @Override
    public String toString() {
        return String.format("(line: %d, start: %d, end:%d, type:%s, modifier:%d)",
                line, start, end, tokenType.toString(), modifier);
    }

    public int getLine() {
        return line;
    }

    public int getStart(){
        return start;
    }

    public int getEnd() {
        return end;
    }

    public SemanticTokenType getType(){
        return tokenType;
    }
}
