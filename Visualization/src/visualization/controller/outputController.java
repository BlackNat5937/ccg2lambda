package visualization.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import visualization.Tools;

import java.util.List;

public class outputController {

    private List<String> listFormula;

    @FXML
    private ListView<String> listViewFormula;

    private ObservableList<String> listFormulaItems = FXCollections.observableArrayList();


    /**
     * Initialize the window, load the formula
     */
    @FXML
    public void initialize()
    {
        listFormula = Tools.getSemanticsFormulas(inputController.getSemanticsXmlFile());
        listFormulaItems.addAll(listFormula);
        listViewFormula.setItems(listFormulaItems);
    }

}
