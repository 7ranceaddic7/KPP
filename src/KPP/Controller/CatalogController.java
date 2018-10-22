package KPP.Controller;

import KPP.Event.CatalogEvent;
import KPP.Model.Catalog;
import KPP.Model.Category;
import KPP.Model.Directory;
import KPP.Service.Broker;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
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
    public ComboBox<Category> cat;
    public ComboBox<Directory> dir;
    public TextField trm;
    public GridPane pane;
    public Label amount;

    public Button root;
    public Button app;
    public Button exp;
    public Button imp;
    public Button res;
    public Button rev;

    private Catalog catalog = new Catalog();

    public void initialize(URL location, ResourceBundle resources) {
        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(30);
        pane.getColumnConstraints().add(col);
        col = new ColumnConstraints();
        col.setPercentWidth(35);
        pane.getColumnConstraints().add(col);
        pane.getColumnConstraints().add(col);

        Broker.listen(CatalogEvent.UPDATE, this::onUpdate);

        root.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onRoot);
        exp.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onExport);
        imp.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onImport);
        app.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> Broker.dispatch(new CatalogEvent(CatalogEvent.APPLY)));
        res.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> Broker.dispatch(new CatalogEvent(CatalogEvent.RESOLVE)));
        rev.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> Broker.dispatch(new CatalogEvent(CatalogEvent.REVERT)));

        dir.valueProperty().addListener((observable, oldValue, newValue) -> onDirectory(newValue));
        cat.valueProperty().addListener(observable -> onFilter());
        trm.textProperty().addListener(observable -> onFilter());
    }

    private void onDirectory(Directory directory) {
        if (null == directory) return;
        Broker.dispatch(new CatalogEvent(CatalogEvent.DIRECTORY).setDirectory(directory));

        trm.setText("");
        amount.setText(String.valueOf(catalog.getParts().size()));
        cat.setItems(FXCollections.observableArrayList(catalog.getCategories()));
        cat.getSelectionModel().selectFirst();
    }

    private void onFilter()
    {
        Category category = cat.getSelectionModel().getSelectedItem();
        String term = trm.getText();
        Broker.dispatch(new CatalogEvent(CatalogEvent.FILTER).setCategory(category).setTerm(term));
    }

    private void onImport(MouseEvent event) {
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(pane.getParent().getScene().getWindow());
        if (null != file) Broker.dispatch(new CatalogEvent(CatalogEvent.IMPORT).setFile(file));
    }

    private void onExport(MouseEvent event) {
        FileChooser chooser = new FileChooser();
        File file = chooser.showSaveDialog(pane.getParent().getScene().getWindow());
        if (null != file) Broker.dispatch(new CatalogEvent(CatalogEvent.EXPORT).setFile(file));
    }

    private void onRoot(MouseEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        File file = chooser.showDialog(pane.getParent().getScene().getWindow());
        if (null == file) return;

        root.setText(file.getAbsolutePath());
        dir.setDisable(false);
        cat.setDisable(false);
        trm.setDisable(false);
        exp.setDisable(false);
        imp.setDisable(false);

        dir.setItems(FXCollections.observableArrayList(catalog.getDirectories(file)));
        dir.getSelectionModel().selectFirst();
    }

    private void onUpdate(CatalogEvent event) {
        Catalog catalog = event.getCatalog();
        app.setDisable(catalog.isActual());
        res.setDisable(!catalog.hasDuplicates());
        rev.setDisable(catalog.isActual());
    }
}
