module com.itiscaleb.cpcompound {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;
    requires java.net.http;
    requires com.google.gson;
    requires org.eclipse.lsp4j;
    requires org.eclipse.lsp4j.jsonrpc;
    requires org.apache.logging.log4j;
    requires MaterialFX;
    requires org.fxmisc.flowless;


    exports com.itiscaleb.cpcompound;
    exports com.itiscaleb.cpcompound.utils;
    exports com.itiscaleb.cpcompound.langServer;
    exports com.itiscaleb.cpcompound.editor;
    exports com.itiscaleb.cpcompound.controller  to javafx.fxml;
    opens com.itiscaleb.cpcompound;
    opens com.itiscaleb.cpcompound.controller;

}