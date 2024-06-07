package com.itiscaleb.cpcompound.langServer;

public enum Language {
    CPP("cpp"), C("c"), Python("python"), JSON("json"), None("none");

    Language(String s) {
        this.lang = s;
    }
    public final String lang;

    @Override
    public String toString() {
        return this.lang;
    }
}
