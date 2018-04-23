package visualization.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import visualization.Main;
import visualization.utils.Tools;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class OutputController {
    /**
     * ccg2lambda tree representations tab.
     */
    @FXML
    public Tab treeTab;
    /**
     * Box representations container.
     */
    @FXML
    public VBox boxCont;
    /**
     * Graph representations container.
     */
    @FXML
    public VBox graphCont;
    /**
     * ccg2lambda tree representations container.
     */
    @FXML
    private WebView treeCont;
    /**
     * Lambdas/Base Sentences list.
     */
    private List<String[]> lambdaList;

    @FXML
    private ListView<String> lambdaListView;

    private ObservableList<String> lambdaListViewItems = FXCollections.observableArrayList();


    /**
     * Initialize the window, load the formula
     */
    @FXML
    public void initialize() {
        lambdaList = Tools.getSemanticsFormulas(Main.xmlSemanticsFile);
        for (String[] strings : lambdaList) {
            lambdaListViewItems.add(strings[0]);
        }
        lambdaListViewItems.addAll();
        lambdaListView.setItems(lambdaListViewItems);

        System.out.println(lambdaListViewItems);

        if (Main.applicationMode == Tools.ApplicationModes.UI)
            displayTreeFromHtml();

        for (String[] s : lambdaList) {
            TitledPane boxPane = initBox(s);
            TitledPane graphPane = initGraph(s);
            boxPane.setExpanded(false);
            graphPane.setExpanded(false);
            boxCont.getChildren().add(boxPane);
            graphCont.getChildren().add(graphPane);
        }
        ((TitledPane) boxCont.getChildren().get(0)).setExpanded(true);
        ((TitledPane) graphCont.getChildren().get(0)).setExpanded(true);
    }

    public void displayTreeFromHtml() {
        treeTab.setDisable(false);
        final WebEngine webEngine = treeCont.getEngine();
        treeCont.setZoom(2.0);
        webEngine.load(Paths.get("../sentences.html").toUri().toString());

    }

    private TitledPane getLoadedPane(String[] formula, String viewPath) {
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

    private TitledPane initGraph(String... formula) {
        TitledPane loadedPane;

        loadedPane = getLoadedPane(formula, "../view/graph.fxml");
        return loadedPane;
    }

    private TitledPane initBox(String... formula) {
        TitledPane loadedPane;

        loadedPane = getLoadedPane(formula, "../view/box.fxml");
        return loadedPane;
    }
}
