package com.itiscaleb.cpcompound.langServer;

public enum SemanticTokenModifier {
    Declaration, Definition, Readonly, Static, Deprecated,
    Abstract, Async, Modification, Documentation, DefaultLibrary;
    public boolean check(int i){
        return (i & (1 << this.ordinal())) != 0;
    }
}
