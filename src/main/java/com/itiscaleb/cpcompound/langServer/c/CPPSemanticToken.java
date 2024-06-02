package com.itiscaleb.cpcompound.langServer.c;

import com.itiscaleb.cpcompound.langServer.SemanticToken;
import com.itiscaleb.cpcompound.langServer.SemanticTokenType;

import java.util.ArrayList;
import java.util.List;

public class CPPSemanticToken{

    public static List<SemanticToken> fromIntList(List<Integer> intList) {
        List<SemanticToken> list = new ArrayList<>();
        int lastLine = 0;
        for (int i=0;i<intList.size();i+=5){
            lastLine += intList.get(i);
            int start = intList.get(i+1);
            int end = start + intList.get(i+2);
            SemanticTokenType type = CPPSemanticTokenType.fromInt(intList.get(i+3));
            list.add(new SemanticToken(lastLine, start, end, type, intList.get(i+4)));
        }
        return list;
    }

}
