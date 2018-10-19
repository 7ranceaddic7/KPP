package KPP.Controller;

import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    public PartlistController activeController;
    public PartlistController purgedController;
    public CatalogController catalogController;
    public PropertyController propertyController;
    public GridPane broker;

    public void initialize(URL location, ResourceBundle resources) {
        catalogController.bind(broker);
        activeController.bind(broker).setActive(true);
        purgedController.bind(broker).setActive(false);
        propertyController.bind(broker);
    }
}
