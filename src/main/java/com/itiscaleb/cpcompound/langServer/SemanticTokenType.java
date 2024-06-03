package com.itiscaleb.cpcompound.langServer;

import java.util.Locale;

public enum SemanticTokenType {
    Namespace, Type, Class, Enum, Interface, Struct, TypeParameter, Parameter,
    Variable, Property, EnumMember, Event, Function, Method, Macro,
    Keyword, Modifier, Comment, String, Number, Regexp, Operator, Decorator;
    static final SemanticTokenType[] value = SemanticTokenType.values();

    public static SemanticTokenType fromString(String str){
        for (SemanticTokenType type: value){
            if(str.toUpperCase(Locale.ROOT).equals(type.toString().toUpperCase(Locale.ROOT))){
                return type;
            }
        }
        return null;
    }

}
