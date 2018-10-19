package KPP.Controller;

import KPP.Event.CatalogEvent;
import KPP.Model.Catalog;
import KPP.Model.Category;
import KPP.Model.Directory;
import javafx.collections.FXCollections;
import javafx.event.EventType;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class CatalogController implements Initializable {
    public ComboBox<Category> category;
    public ComboBox<Directory> directory;
    public GridPane pane;
    public TextField term;
    public Button revert;
    public Button apply;
    public Button root;
    public Label amount;

    private Catalog catalog;
    private Node broker;


    public Button exportAction;
    public Button importAction;
    public Button resolveAction;

    public void initialize(URL location, ResourceBundle resources) {
        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(30);
        pane.getColumnConstraints().add(col);
        col = new ColumnConstraints();
        col.setPercentWidth(35);
        pane.getColumnConstraints().add(col);
        pane.getColumnConstraints().add(col);
    }

    void bind(Node broker) {
        this.broker = broker;

        broker.addEventHandler(CatalogEvent.UPDATE, event -> {
            resolveAction.setDisable(!event.getCatalog().hasDuplicates());
            revert.setDisable(event.getCatalog().isActual());
            apply.setDisable(event.getCatalog().isActual());
        });

        root.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            DirectoryChooser chooser = new DirectoryChooser();
            File file = chooser.showDialog(broker.getScene().getWindow());
            if (null == file) return;

            catalog = new Catalog();
            catalog.bind(broker);

            onChangeRoot(file);

            exportAction.setDisable(false);
            importAction.setDisable(false);
        });

        exportAction.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            FileChooser chooser = new FileChooser();
            File file = chooser.showSaveDialog(broker.getScene().getWindow());
            if (null == file) return;
            broker.fireEvent(createEvent(CatalogEvent.EXPORT).setFile(file));
        });

        importAction.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            FileChooser chooser = new FileChooser();
            File file = chooser.showOpenDialog(broker.getScene().getWindow());
            if (null == file) return;
            broker.fireEvent(createEvent(CatalogEvent.IMPORT).setFile(file));
        });

        resolveAction.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> broker.fireEvent(createEvent(CatalogEvent.RESOLVE)));
        revert.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> broker.fireEvent(createEvent(CatalogEvent.REVERT)));
        apply.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> broker.fireEvent(createEvent(CatalogEvent.APPLY)));

        directory.valueProperty().addListener(((observable, oldValue, newValue) -> onChangeDirectory(newValue)));
        category.valueProperty().addListener(((observable, oldValue, newValue) -> onChangeCategory(newValue)));
        term.textProperty().addListener(((observable, oldValue, newValue) -> onChangeSearch(newValue)));
    }

    private void onChangeCategory(Category category) {
        broker.fireEvent(createEvent(CatalogEvent.CATEGORY).setCategory(category));
    }

    private void onChangeDirectory(Directory directory) {
        broker.fireEvent(createEvent(CatalogEvent.DIRECTORY).setDirectory(directory));
        category.setItems(FXCollections.observableArrayList(catalog.getCategories()));
        category.getSelectionModel().selectFirst();
        amount.setText(String.valueOf(catalog.getParts().size()));
    }

    private void onChangeRoot(File file) {
        broker.fireEvent(createEvent(CatalogEvent.ROOT).setFile(file));
        directory.setItems(FXCollections.observableArrayList(catalog.getDirectories(file)));
        directory.getSelectionModel().selectFirst();

        root.setText(file.getAbsolutePath());
        directory.setDisable(false);
        category.setDisable(false);
        term.setDisable(false);
    }

    private void onChangeSearch(String term) {
        broker.fireEvent(createEvent(CatalogEvent.SEARCH).setTerm(term));
    }

    private CatalogEvent createEvent(EventType<CatalogEvent> type) {
        CatalogEvent event = new CatalogEvent(type);

        event.setDirectory(directory.getSelectionModel().getSelectedItem());
        event.setCategory(category.getSelectionModel().getSelectedItem());
        event.setCatalog(catalog);
        event.setFile(new File(root.getText()));
        event.setTerm(term.getText());

        return event;
    }
}
