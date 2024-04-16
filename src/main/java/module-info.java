module com.itiscaleb.cpcompound {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;
    requires java.net.http;
    requires com.google.gson;


    opens com.itiscaleb.cpcompound to javafx.fxml;
    exports com.itiscaleb.cpcompound;
}