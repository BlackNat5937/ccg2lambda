package visualization.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import visualization.Tools;
import visualization.box.Box;
import visualization.box.Element;
import visualization.box.ElementType;
import visualization.graph.Node;
import visualization.graph.NodeType;

import visualization.graph.Graph;
import java.util.List;

public class outputController {

    /**
     * Graph
     */
    @FXML
    private AnchorPane anchorPaneGraph;


    /**
     * Box
     */
    @FXML
    private VBox boxBox = new VBox();

    /**
     * Formula
     */
    private List<String> listFormula;

    @FXML
    private ListView<String> listViewFormula;

    private ObservableList<String> listFormulaItems = FXCollections.observableArrayList();


    /**
     * Initialize the window, load the formula
     */
    @FXML
    public void initialize() {
        listFormula = Tools.getSemanticsFormulas(Tools.xmlSemanticsFile);
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
        Element eat = new Element("e1", "eat", ElementType.VERB);
        eat.addLink(shark, fish);
        b.getElements().add(shark);
        b.getElements().add(fish);
        b.getElements().add(eat);
        System.out.println(b.toString());


        boxBox.setPadding(new Insets(10, 50, 50, 50));
        boxBox.setSpacing(10);

        Label lb1 = new Label(b.getTitle());
        lb1.setFont(Font.font("Amble CN", FontWeight.BOLD, 24));
        boxBox.getChildren().add(lb1);


        int u = b.getBody().size();

        for(int i = 0;  i < u; i++)
        {
            boxBox.getChildren().add(new Label(b.getBody().get(i)));
        }

    }
}
