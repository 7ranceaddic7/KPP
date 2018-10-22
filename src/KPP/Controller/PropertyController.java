package KPP.Controller;

import KPP.Event.CatalogEvent;
import KPP.Model.Part;
import KPP.Model.Property;
import KPP.Service.Broker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PropertyController implements Initializable {
    public TableView<Property> table;
    public Label amount;

    public void initialize(URL location, ResourceBundle resources) {
        Broker.listen(CatalogEvent.DISPLAY, this::onDisplay);
    }

    private void onDisplay(CatalogEvent event)
    {
        List<Part> parts = event.getParts();

        if (!parts.isEmpty()) {
            ObservableList<Property> properties = FXCollections.observableArrayList(parts.get(0).getProperties());
            amount.setText(String.valueOf(properties.size()));
            table.setItems(properties);
            return;
        }

        amount.setText("");
        table.getItems().clear();
    }
}
