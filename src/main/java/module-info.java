module com.itiscaleb.cpcompound {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.itiscaleb.cpcompound to javafx.fxml;
    exports com.itiscaleb.cpcompound;
}