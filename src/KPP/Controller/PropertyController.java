package KPP.Controller;

import KPP.Event.CatalogEvent;
import KPP.Model.Part;
import KPP.Model.Property;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PropertyController implements Initializable {
    public TableView<Property> table;
    public Button edit;
    public Label amount;

    private Part part;

    public void initialize(URL location, ResourceBundle resources) {
        edit.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            try {
                if (null != part){
                    Desktop.getDesktop().open(new File(part.getValues().get("file")));
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    PropertyController bind(Node broker)
    {
        broker.addEventHandler(CatalogEvent.DISPLAY, e -> {
            List<Part> parts = e.getParts();
            if (null == parts || parts.isEmpty()) return;

            part = parts.get(0);
            edit.setDisable(false);
            table.setItems(FXCollections.observableArrayList(part.getProperties()));
            amount.setText(String.valueOf(table.getItems().size()));
        });

        broker.addEventHandler(CatalogEvent.UPDATE, event -> {
            part = null;
            edit.setDisable(true);
            table.getItems().clear();
            amount.setText("");
        });

        return this;
    }
}
