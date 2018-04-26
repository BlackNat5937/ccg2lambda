package visualization.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import visualization.Main;
import visualization.utils.Tools;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class OutputController implements Stageable {
    /**
     * The view this controller manages.
     */
    private Stage view;
    /**
     * The container for both the visualisation panels and the formulas panel.
     */
    @FXML
    public SplitPane splitContainer;
    /**
     * TabPane containing a Tab per representation.
     */
    @FXML
    public TabPane tabPanel;
    /**
     * graph representations tab.
     */
    @FXML
    public Tab graphTab;
    /**
     * DRS representations tab.
     */
    @FXML
    public Tab boxTab;
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
     * tree representation container
     */
    @FXML
    private VBox treeCont;
    /**
     * ccg2lambda tree representations container.
     */
    @FXML
    private WebView treeContHtml;
    /**
     * Currently selected tab index.
     */
    private int currentTabIndex = 0;
    /**
     * View for the list of all Sentences.
     */
    @FXML
    private ListView<String> lambdaListView;
    /**
     * Lambdas/Base Sentences list.
     */
    private List<String[]> lambdaList;
    /**
     * Observable list for all the sentences.
     */
    private ObservableList<String> lambdaListViewItems = FXCollections.observableArrayList();


    /**
     * Initializes the window, loads the formulas
     */
    @FXML
    public void initialize() {
        getLambdas();
        initVisualizations();
        initControls();
    }

    /**
     * Initializes the controls for interactivity with the user.
     */
    private void initControls() {
        lambdaListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        lambdaListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String selected = lambdaListView.getSelectionModel().getSelectedItems().get(0);
            int selectedIndex = lambdaListViewItems.indexOf(selected);
            changeFocusedElement(selectedIndex);
        });
        tabPanel.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) ->
                        currentTabIndex = tabPanel.getSelectionModel().getSelectedIndex());
    }

    /**
     * Changes the element which has the focus in the visualization.
     *
     * @param selectedIndex the index of the selected element
     */
    private void changeFocusedElement(int selectedIndex) {
        Pane[] containers = {
                boxCont,
                graphCont,
                treeCont
        };
        for (Pane container : containers) {
            container.getChildren().forEach(node -> {
                TitledPane titledPane = (TitledPane) node;
                titledPane.setExpanded(false);
            });
            ((TitledPane) container.getChildren().get(selectedIndex)).setExpanded(true);
        }
    }

    /**
     * Initializes the visualizations.
     */
    private void initVisualizations() {
        if (Main.applicationMode == Tools.ApplicationModes.UI)
            displayTreeFromHtml();

        for (String[] s : lambdaList) {
            TitledPane boxPane = initBox(s);
            TitledPane graphPane = initGraph(s);
            TitledPane treePane = initTree(s);
            boxPane.setExpanded(false);
            graphPane.setExpanded(false);
            treePane.setExpanded(false);
            boxCont.getChildren().add(boxPane);
            graphCont.getChildren().add(graphPane);
            treeCont.getChildren().add(treePane);
        }
        ((TitledPane) boxCont.getChildren().get(0)).setExpanded(true);
        ((TitledPane) graphCont.getChildren().get(0)).setExpanded(true);
        ((TitledPane) treeCont.getChildren().get(0)).setExpanded(true);
    }

    /**
     * Gets the lambda formulas from the xml file generated by ccg2lambda or given as an argument.
     */
    private void getLambdas() {
        lambdaList = Tools.getSemanticsFormulas(Main.xmlSemanticsFile);
        for (String[] strings : lambdaList) {
            lambdaListViewItems.add(strings[0]);
        }
        lambdaListViewItems.addAll();
        lambdaListView.setItems(lambdaListViewItems);
    }

    /**
     * Initialises the tree display using the html document generated by ccg2lambda.
     */
    private void displayTreeFromHtml() {
        treeTab.setDisable(false);
        final WebEngine webEngine = treeContHtml.getEngine();
        treeContHtml.setZoom(2.0);
        switch (Main.selectedTemplateType) {

            case CLASSIC:
                webEngine.load(Paths.get("../sentences.html").toUri().toString());

                break;
            case EVENT:
                webEngine.load(Paths.get("../results/sentences.txt.html").toUri().toString());

                break;
        }

    }

    /**
     * Gets an instance of a view for a given representation and lambda.
     *
     * @param lambda   the lambda used to compute a representation.
     * @param viewPath the path to the view
     * @return a TitledPane having for title the lambda given
     */
    private TitledPane getLoadedPane(String[] lambda, String viewPath) {
        TitledPane loadedPane = null;
        Parametrable<String> stringParametrable;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(viewPath));

        try {
            loadedPane = fxmlLoader.load();
            stringParametrable = fxmlLoader.getController();
            stringParametrable.initData(lambda);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loadedPane;
    }

    /**
     * Initializes a graph representation of a lambda.
     *
     * @param lambda the lambda to get a representation of
     * @return a TitledPane with the graph representation of the lambda
     */
    private TitledPane initGraph(String... lambda) {
        TitledPane loadedPane = getLoadedPane(lambda, "../view/graph.fxml");
        ;
        return loadedPane;
    }

    /**
     * Initializes a DRS representation of a lambda.
     *
     * @param lambda the lambda to get a representation of
     * @return a TitledPane with the DRS representation of the lambda
     */
    private TitledPane initBox(String... lambda) {
        TitledPane loadedPane = getLoadedPane(lambda, "../view/box.fxml");
        return loadedPane;
    }

    /**
     * Initializes a Tree representation of a lambda
     *
     * @param lambda the lambda to get a representation of
     * @return a TitledPane with the Tree representation of the lambda
     */
    private TitledPane initTree(String... lambda) {
        TitledPane loadedPane = getLoadedPane(lambda, "../view/tree.fxml");
        return loadedPane;
    }

    /**
     * Enables stage events processing in this controller by providing the controller in an encapsulated method.
     *
     * @param primaryStage the stage this controller managess
     */
    @Override
    public void initStage(Stage primaryStage) {
        this.view = primaryStage;
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            Platform.runLater(this::setDividerPosition);
            /*System.out.println("divider : " + splitContainer.getDividers().get(0).getPosition());
        System.out.println("test initStage");*/
        };
        view.widthProperty().addListener(stageSizeListener);
        view.heightProperty().addListener(stageSizeListener);
    }

    /**
     * Sets the position of the divider for the SplitPane.
     */
    private void setDividerPosition() {
        splitContainer.setDividerPositions(0.8);
    }
}
