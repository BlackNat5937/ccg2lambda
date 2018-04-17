package visualization.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import visualization.Tools;
import visualization.box.Box;
import visualization.box.Element;
import visualization.box.ElementType;
import visualization.graph.Graph;
import visualization.graph.Node;
import visualization.graph.NodeType;

import java.util.List;

public class outputController {

    @FXML
    private AnchorPane anchorPaneGraph;

 /*   @FXML
    private TableView boxView;*/

    private List<String> listFormula;

    @FXML
    private ListView<String> listViewFormula;

    private ObservableList<String> listFormulaItems = FXCollections.observableArrayList();


    /**
     * Initialize the window, load the formula
     */
    @FXML
    public void initialize() {
        listFormula = Tools.getSemanticsFormulas(inputController.getSemanticsXmlFile());
        listFormulaItems.addAll(listFormula);
        listViewFormula.setItems(listFormulaItems);


        Graph g = new Graph();
        Node test = new Node("test", NodeType.NOUN);
        Node test2 = new Node("test2", NodeType.EVENT, test, "is a");
        Node test3 = new Node("test3", NodeType.NOUN);
        test2.addLink(test3, "le lien");
        g.getNodes().add(test);
        g.getNodes().add(test2);
        g.getNodes().add(test3);
        System.out.println(g.toString());

        anchorPaneGraph.getChildren().add(g.generateCanvas());


        Box b = new Box();
        Element shark = new Element("x", "shark", ElementType.SUBJECT);
        Element fish = new Element("z1", "fish", ElementType.NOUN);
        Element eat  = new Element("e1", "eat", ElementType.VERB);
        eat.addLink(shark, fish);
        b.getElements().add(shark);
        b.getElements().add(fish);
        b.getElements().add(eat);
        System.out.println(b.toString());

     //   boxView.getItems().add(1, b.toString());

    }
}
