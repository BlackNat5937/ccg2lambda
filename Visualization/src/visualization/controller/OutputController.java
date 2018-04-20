package visualization.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import visualization.box.Box;
import visualization.box.Element;
import visualization.box.ElementType;
import visualization.utils.Tools;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.List;

public class OutputController {

    @FXML
    public VBox boxCont;
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
     * Tree
     */
    @FXML
    private WebView treeView;

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
        displayTreeFromHtml();

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

       // tabBox.setStyle(cssLayout);

        Label lb1 = new Label(b.getTitle());
        lb1.setFont(Font.font("Amble CN", FontWeight.BOLD, 24));
        tabBox.getChildren().add(lb1);


        int u = b.getBody().size();

        for (int i = 0; i < u; i++) {
            tabBox.getChildren().add(new Label(b.getBody().get(i)));
        }

        for (String s : listFormula) {
            TitledPane boxPane = initBox(s);
            TitledPane graphPane = initGraph(s);
            boxCont.getChildren().add(boxPane);
            graphCont.getChildren().add(graphPane);
        }
    }

    public void displayTreeFromHtml(){
        final WebEngine webEngine = treeView.getEngine();
        treeView.setZoom(2.0);
        webEngine.load(Paths.get("../sentences.html").toUri().toString());

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

    private TitledPane getLoadedPane(String formula, String viewPath) {
        TitledPane loadedPane = null;
        Parametrable<String> stringParametrable;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(viewPath));

        try {
            loadedPane = fxmlLoader.load();
            stringParametrable = fxmlLoader.getController();
            stringParametrable.initData(formula);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loadedPane;
    }

    private TitledPane initGraph(String formula) {
        TitledPane loadedPane = null;
        Parametrable<String> gCon = null;

        loadedPane = getLoadedPane(formula, "../view/graph.fxml");
        return loadedPane;
    }

    private TitledPane initBox(String formula) {
        TitledPane loadedPane = null;
        Parametrable<String> bCon = null;

        loadedPane = getLoadedPane(formula, "../view/box.fxml");
        return loadedPane;
    }

    public void generateGraph() {
        for (String s : listFormula) {
            initGraph(s);
        }
    }
}
