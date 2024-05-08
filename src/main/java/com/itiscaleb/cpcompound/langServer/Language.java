package com.itiscaleb.cpcompound.langServer;

public enum Language {
    CPP("c++"), C("c"), Python("python"), None("none");

    Language(String s) {
        this.lang = s;
    }
    public final String lang;
}
