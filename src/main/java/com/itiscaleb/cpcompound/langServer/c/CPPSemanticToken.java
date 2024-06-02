package com.itiscaleb.cpcompound.langServer.c;

import com.itiscaleb.cpcompound.langServer.SemanticToken;
import com.itiscaleb.cpcompound.langServer.SemanticTokenType;

import java.util.ArrayList;
import java.util.List;

public class CPPSemanticToken{

    public static List<SemanticToken> fromIntList(List<Integer> intList) {
        List<SemanticToken> list = new ArrayList<>();
        int lastLine = 0;
        int lastStart = 0;
        for (int i=0;i<intList.size();i+=5){
            int deltaLine = intList.get(i);
            lastLine += deltaLine;
            if(deltaLine > 0){
                lastStart = 0;
            }
            lastStart += intList.get(i+1);
            int end = lastStart + intList.get(i+2);
            SemanticTokenType type = CPPSemanticTokenType.fromInt(intList.get(i+3));
            list.add(new SemanticToken(lastLine, lastStart, end, type, intList.get(i+4)));
        }
        return list;
    }

}
