package CphPack;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class DemoController {
    @FXML
    Label la;

    @FXML
    Button bu;

    public void onUp(){
        bu.setScaleX(bu.getScaleX()*1.5);
    }


}
