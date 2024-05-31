package com.itiscaleb.cpcompound.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class TestcaseCompare {

    public boolean nonStrictMatch(String a, String b) {
        List<String> va = new ArrayList<>(Arrays.asList(a.split("\\s+")));
        List<String> vb = new ArrayList<>(Arrays.asList(b.split("\\s+")));
        return va.equals(vb);
    }

    public boolean strictMatch(String str1, String str2) {
        return str1.equals(str2);
    }

}
