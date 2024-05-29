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
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.whhg;
    requires org.kordamp.ikonli.fluentui;
    requires org.kordamp.ikonli.carbonicons;
    requires org.kordamp.ikonli.codicons;
    requires org.kordamp.ikonli.boxicons;
    requires org.kordamp.ikonli.antdesignicons;
    requires org.kordamp.ikonli.materialdesign;
    requires org.kordamp.ikonli.runestroicons;
    requires org.kordamp.ikonli.coreui;
    requires org.kordamp.ikonli.lineawesome;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.ikonli.evaicons;
    requires org.kordamp.ikonli.dashicons;
    requires sevenzipjbinding;

    exports com.itiscaleb.cpcompound;
    exports com.itiscaleb.cpcompound.utils;
    exports com.itiscaleb.cpcompound.langServer;
    exports com.itiscaleb.cpcompound.editor;
    exports com.itiscaleb.cpcompound.controller to javafx.fxml;
    opens com.itiscaleb.cpcompound;
    opens com.itiscaleb.cpcompound.controller;
    exports com.itiscaleb.cpcompound.downloader;
    exports com.itiscaleb.cpcompound.fileSystem;
    exports com.itiscaleb.cpcompound.component;
}