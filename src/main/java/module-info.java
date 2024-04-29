module com.itiscaleb.cpcompound {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;
    requires java.net.http;
    requires com.google.gson;
    requires org.eclipse.lsp4j.jsonrpc;
    requires org.eclipse.lsp4j;


    opens com.itiscaleb.cpcompound to javafx.fxml;
    exports com.itiscaleb.cpcompound;
    exports com.itiscaleb.cpcompound.utils;
}