package KPP.Controller;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    public PartlistController activeController;
    public PartlistController purgedController;

    public void initialize(URL location, ResourceBundle resources) {
        activeController.setActive(true);
        purgedController.setActive(false);
    }
}
