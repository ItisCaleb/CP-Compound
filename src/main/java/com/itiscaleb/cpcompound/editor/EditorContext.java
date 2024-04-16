package com.itiscaleb.cpcompound.editor;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EditorContext {
    StringProperty code = new SimpleStringProperty();

    public String getCode(){
        return code.get();
    }

    public StringProperty getStringProperty(){
        return code;
    }

}
