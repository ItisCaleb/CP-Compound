package com.itiscaleb.cpcompound.langServer.cpp;

import com.itiscaleb.cpcompound.langServer.SemanticTokenType;

import java.util.List;

public class CPPSemanticTokenType {
    static SemanticTokenType[] value;

    public static void init(List<String> types){
        value = new SemanticTokenType[types.size()];
        for(int i=0;i<types.size();i++){
            value[i] = SemanticTokenType.fromString(types.get(i));
        }
    }

    public static SemanticTokenType fromInt(int i){
        return value[i];
    }
}
