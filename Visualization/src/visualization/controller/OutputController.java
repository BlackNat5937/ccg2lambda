package visualization.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import visualization.Tools;
import visualization.box.Box;
import visualization.box.Element;
import visualization.box.ElementType;

import java.io.IOException;
import java.util.List;

public class OutputController {

    @FXML
    public Pane container;
    @FXML
    public VBox cont;
    /**
     * Graph
     */
    @FXML
    public VBox graphCont;

    /**
     * Box
     */
    @FXML
    private VBox tabBox = new VBox();

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

        System.out.println(listFormulaItems);

        generateGraph();

        int v = listFormula.size();


        Box b = getBox();


        tabBox.setPadding(new Insets(10, 50, 50, 50));
        tabBox.setSpacing(10);

        String cssLayout = "-fx-padding: 20;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: black;";

        tabBox.setStyle(cssLayout);

        Label lb1 = new Label(b.getTitle());
        lb1.setFont(Font.font("Amble CN", FontWeight.BOLD, 24));
        tabBox.getChildren().add(lb1);


        int u = b.getBody().size();

        for (int i = 0; i < u; i++) {
            tabBox.getChildren().add(new Label(b.getBody().get(i)));
        }

        for (String s : listFormula) {
            initBox(s);
        }
    }

    private Box getBox() {
        Box b = new Box();
        Element shark = new Element("x", "shark", ElementType.SUBJECT);
        Element fish = new Element("z1", "fish", ElementType.NOUN);
        Element eat = new Element("e1", "eat", ElementType.VERB);
        eat.addLink(shark, fish);
        b.getElements().add(shark);
        b.getElements().add(fish);
        b.getElements().add(eat);
        return b;
    }

    private TitledPane initGraph(String formula){
        TitledPane loadedPane = null;
        Parametrable<String> gCon = null;
        FXMLLoader graphLoader = new FXMLLoader(getClass().getResource("../view/graph.fxml"));
        try {
            loadedPane = graphLoader.load();
            gCon = graphLoader.getController();
            gCon.initData(formula);
            graphCont.getChildren().add(loadedPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loadedPane;
    }

    private TitledPane initBox(String formula) {
        TitledPane loadedPane = null;
        Parametrable<String> bCon = null;

        FXMLLoader boxLoader = new FXMLLoader(getClass().getResource("../view/box.fxml"));
        try {
            loadedPane = boxLoader.load();
            bCon = boxLoader.getController();
            bCon.initData(formula);
            cont.getChildren().add(loadedPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loadedPane;
    }

    public void generateGraph(){
        //exists x.((x=_john) & ( _be(_john) & Prog(_walk(_john)))

        /*Graph g = new Graph();
        Node test = new Node("test", NodeType.NOUN);
        Node test2 = new Node("test2", NodeType.EVENT, test, "is a");
        Node test3 = new Node("test3", NodeType.NOUN);
        test2.addLink(test3, "le lien");
        g.getNodes().add(test);
        g.getNodes().add(test2);
        g.getNodes().add(test3);
        System.out.println(g.toString());
    */


        for(String s : listFormula){

            initGraph(s);
            //graphCont.getChildren().add(g.generateCanvas());
        }
    }
}
