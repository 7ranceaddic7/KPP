package KPP.Controller;

import KPP.Event.CatalogEvent;
import KPP.Model.Part;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PartlistController implements Initializable {
    public TableView<Part> table;
    public Button action;
    public Label amount;
    public Label title;

    private FilteredList<Part> filtered;
    private CatalogEvent event;
    private Boolean active;

    public void initialize(URL location, ResourceBundle resources) {
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    PartlistController bind(Node broker)
    {
        table.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (null == event) return;
            List<Part> parts = table.getSelectionModel().getSelectedItems();

            if (1 == e.getClickCount()) {
                broker.fireEvent(event.setType(CatalogEvent.DISPLAY).setParts(parts));
                return;
            }

            if (2 == e.getClickCount()) {
                broker.fireEvent(event.setType(CatalogEvent.TOGGLE).setParts(parts));
                return;
            }
        });

        action.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            List<Part> parts = table.getSelectionModel().getSelectedItems();
            broker.fireEvent(event.setType(CatalogEvent.TOGGLE).setParts(parts));
        });

        broker.addEventHandler(CatalogEvent.UPDATE, this::onUpdate);
        broker.addEventHandler(CatalogEvent.CATEGORY, this::onChangeFilter);
        broker.addEventHandler(CatalogEvent.SEARCH, this::onChangeFilter);

        return this;
    }

    void setActive(boolean active) {
        action.setText(active ? "purge" : "activate");
        title.setText(active ? "Active" : "Purged");
        this.active = active;
    }

    private void onChangeFilter(CatalogEvent e)
    {
        if (null == filtered) return;
        filtered.setPredicate(part -> part.isState(active) && part.matches(e.getTerm()) && part.matches(e.getCategory()));
        amount.setText(String.valueOf(filtered.size()));
        action.setDisable(filtered.isEmpty());
    }

    private void onUpdate(CatalogEvent e)
    {
        filtered = FXCollections.observableArrayList(e.getCatalog().getParts()).filtered(part -> part.isState(active));
        table.setItems(filtered);
        onChangeFilter(event = e);
    }
}
