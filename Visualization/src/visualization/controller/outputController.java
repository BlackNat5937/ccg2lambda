package visualization.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import visualization.Tools;
import visualization.graph.Graph;
import visualization.graph.Link;
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

        Graph g = new Graph();
        g.getNodes().add(new Node("test2",NodeType.NOUN));
        g.getNodes().add(new Node("test", NodeType.EVENT, g.getNodes().get(0), "is a"));
        System.out.println(g.toString());

        g = new Graph();
        Node test = new Node("test2", NodeType.NOUN);
        Node test2 = new Node("test", NodeType.EVENT, test, "is a");
        System.out.println(g.toString());
    }

}
