package KPP.Controller;

import KPP.Event.CatalogEvent;
import KPP.Model.Part;
import KPP.Service.Broker;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.Initializable;
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

    private FilteredList<Part> filtered = new FilteredList<>(FXCollections.observableArrayList());
    private Boolean active;

    public void initialize(URL location, ResourceBundle resources) {
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onSelect);
        action.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onAction);

        Broker.listen(CatalogEvent.UPDATE, this::onUpdate);
        Broker.listen(CatalogEvent.UPDATE, this::onFilter);
        Broker.listen(CatalogEvent.FILTER, this::onFilter);
    }

    void setActive(boolean active) {
        action.setText(active ? "purge" : "activate");
        title.setText(active ? "Active" : "Purged");
        this.active = active;
    }

    private void onAction(MouseEvent event) {
        List<Part> parts = table.getSelectionModel().getSelectedItems();
        Broker.dispatch(new CatalogEvent(CatalogEvent.TOGGLE).setParts(parts));
    }

    private void onFilter(CatalogEvent event) {
        filtered.setPredicate(part -> part.isState(active) && part.matches(event.getTerm()) && part.matches(event.getCategory()));
        amount.setText(String.valueOf(filtered.size()));
        action.setDisable(filtered.isEmpty());
    }

    private void onSelect(MouseEvent event) {
        List<Part> parts = table.getSelectionModel().getSelectedItems();

        if (1 == event.getClickCount()) {
            Broker.dispatch(new CatalogEvent(CatalogEvent.DISPLAY).setParts(parts));
            return;
        }

        Broker.dispatch(new CatalogEvent(CatalogEvent.TOGGLE).setParts(parts));
    }

    private void onUpdate(CatalogEvent event) {
        filtered = FXCollections.observableArrayList(event.getCatalog().getParts()).filtered(part -> part.isState(active));
        table.setItems(filtered);
    }
}
