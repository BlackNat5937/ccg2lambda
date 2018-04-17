package visualization.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import visualization.Tools;
import visualization.graph.Graph;
import visualization.graph.Node;
import visualization.graph.NodeType;

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

/*
        Graph g = new Graph();
        Node test = new Node("test", NodeType.NOUN);
        Node test2 = new Node("test2", NodeType.EVENT, test, "is a");
        Node test3 = new Node("test3", NodeType.NOUN);
        test.addLink(test3, "le lien");
        g.getNodes().add(test);
        g.getNodes().add(test2);
        g.getNodes().add(test3);
        System.out.println(g.toString());
*/
    }

}
