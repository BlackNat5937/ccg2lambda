package visualization.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import visualization.Main;
import visualization.utils.Tools;
import visualization.utils.formula.Formula;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
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
    private ListView<Formula> lambdaListView;
    /**
     * Lambdas/Base Sentences list.
     */
    private List<Formula> formulaList;
    /**
     * Observable list for all the sentences.
     */
    private ObservableList<Formula> lambdaListViewItems = FXCollections.observableArrayList();

    private final ContextMenu contextMenu = new ContextMenu();

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
            Formula selected = lambdaListView.getSelectionModel().getSelectedItems().get(0);
            int selectedIndex = lambdaListViewItems.indexOf(selected);
            changeFocusedElement(selectedIndex);
        });
        tabPanel.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) ->
                        currentTabIndex = tabPanel.getSelectionModel().getSelectedIndex());

        MenuItem item1 = new MenuItem("Print");
        item1.setOnAction(printEvent);
        contextMenu.getItems().add(item1);

        lambdaListView.setContextMenu(contextMenu);
    }

    private final EventHandler<ActionEvent> printEvent = event -> {
        Node n = graphCont.getChildren().get(lambdaListView.getSelectionModel().getSelectedIndex());
            //here save the image
            if (n.getClass() == TitledPane.class) {
                TitledPane tp = (TitledPane) n;
                Node n2 = tp.getContent();
                if (n2.getClass() == ScrollPane.class) {
                    ScrollPane sp = (ScrollPane) n2;
                    Node n3 = sp.getContent();
                    WritableImage wi = new WritableImage((int) n3.getBoundsInLocal().getWidth(), (int) n3.getBoundsInLocal().getHeight());

                    n3.snapshot(new SnapshotParameters(), wi);
                    BufferedImage image = javafx.embed.swing.SwingFXUtils.fromFXImage(wi, null);

                    try {
                        ImageIO.write(image, "png", new File("graph" + graphCont.getChildren().indexOf(n) + ".png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

    };


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

        for (Formula formula : formulaList) {
            TitledPane boxPane = initBox(formula);
            TitledPane graphPane = initGraph(formula);
            //   TitledPane treePane = initTree(formula);
            boxPane.setExpanded(false);
            graphPane.setExpanded(false);
            //   treePane.setExpanded(false);
            boxCont.getChildren().add(boxPane);
            graphCont.getChildren().add(graphPane);
            //    treeCont.getChildren().add(treePane);
        }
        ((TitledPane) boxCont.getChildren().get(0)).setExpanded(true);
        ((TitledPane) graphCont.getChildren().get(0)).setExpanded(true);
        //  ((TitledPane) treeCont.getChildren().get(0)).setExpanded(true);
    }

    /**
     * Gets the lambda formulas from the xml file generated by ccg2lambda or given as an argument.
     */
    private void getLambdas() {
        formulaList = Tools.getSemanticsFormulas(Main.xmlSemanticsFile);
        System.out.println(formulaList);
        lambdaListViewItems.addAll(formulaList);
        lambdaListView.setItems(lambdaListViewItems);
    }

    /**
     * Initialises the tree display using the html document generated by ccg2lambda.
     */
    private void displayTreeFromHtml() {
        treeTab.setDisable(false);
        final WebEngine webEngine = treeContHtml.getEngine();
        treeContHtml.setZoom(2.0);
        switch (Main.selectedParserType) {
            case CANDC:
                switch (Main.selectedTemplateType) {
                    case CLASSIC:
                        webEngine.load(Paths.get("../results/sentences.txt.html").toUri().toString());
                        break;
                    case EVENT:
                        webEngine.load(Paths.get("../results/sentences.txt.html").toUri().toString());
                        break;
                }
                break;

            case ALL:
                switch (Main.selectedTemplateType) {
                    case CLASSIC:
                        webEngine.load(Paths.get("../en_results/sentences.txt.html").toUri().toString());
                        break;
                    case EVENT:
                        webEngine.load(Paths.get("../en_results/sentences.txt.html").toUri().toString());
                        break;
                }
                break;

            case JA:
                switch (Main.selectedTemplateType) {
                    case CLASSIC:
                        webEngine.load(Paths.get("../ja_results/sentences.txt.html").toUri().toString());
                        break;
                    case EVENT:
                        webEngine.load(Paths.get("../ja_results/sentences.txt.html").toUri().toString());
                        break;
                }
                break;
        }


    }

    /**
     * Gets an instance of a view for a given representation and formula.
     *
     * @param formula  the formula used to compute a representation.
     * @param viewPath the path to the view
     * @return a TitledPane having for title the formula given
     */
    private TitledPane getLoadedPane(Formula formula, String viewPath) {
        TitledPane loadedPane = null;
        Parametrable<Formula> formulaParametrable;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(viewPath));

        try {
            loadedPane = fxmlLoader.load();
            formulaParametrable = fxmlLoader.getController();
            formulaParametrable.initData(formula);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loadedPane;
    }

    /**
     * Initializes a graph representation of a formula.
     *
     * @param formula the formula to get a representation of
     * @return a TitledPane with the graph representation of the formula
     */
    private TitledPane initGraph(Formula formula) {
        return getLoadedPane(formula, "../view/graph.fxml");
    }

    /**
     * Initializes a DRS representation of a formula.
     *
     * @param formula the formula to get a representation of
     * @return a TitledPane with the DRS representation of the formula
     */
    private TitledPane initBox(Formula formula) {
        return getLoadedPane(formula, "../view/box.fxml");
    }

    /**
     * Initializes a Tree representation of a formula
     *
     * @param formula the formula to get a representation of
     * @return a TitledPane with the Tree representation of the formula
     */
    private TitledPane initTree(Formula formula) {
        return getLoadedPane(formula, "../view/tree.fxml");
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
