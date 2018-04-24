package visualization.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import visualization.Main;
import visualization.utils.Tools;

import java.io.*;
import java.util.Objects;

/**
 * Controller for the input view.
 *
 * @author Nathan Joubert
 * @author Thomas Guesdon
 * @author Gaétan Basile
 * @see visualization.view
 */
public class InputController implements Stageable {
    /**
     * Button for adding a sentence to the list.
     */
    @FXML
    public Button addSentenceButton;
    /**
     * Button for starting the processing of the sentences in the list.
     */
    @FXML
    public Button startProcessingButton;
    /**
     * MenuItem for setting the location of ccg2lambda.
     */
    @FXML
    public MenuItem setccg2lambdaLocationItem;
    /**
     * MenuItem for showing information about the software.
     */
    @FXML
    public MenuItem showInformationItem;
    /**
     * MenuItem for showing the readme for ccg2lambda (on the web)
     */
    @FXML
    public MenuItem showReadMeItem;
    /**
     * TextField enabling the input of sentences from the user.
     */
    @FXML
    private TextField sentenceField;
    /**
     * Progress bar indicating the completion status of the task.
     */
    @FXML
    private ProgressBar visualizationProgressBar;
    /**
     * Progress of the conversion process.
     */
    private SimpleDoubleProperty progress = new SimpleDoubleProperty(0.0);
    /**
     * View of all the sentences the user has input.
     */
    @FXML
    private ListView<String> listSentences;
    /**
     * List of all the sentences.
     */
    private ObservableList<String> listSentencesItems = FXCollections.observableArrayList();

    /**
     * Enables knowing if the host OS is windows.
     */
    private final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    /**
     * The view this controller manages.
     */
    private Stage view;


    /**
     * Initializes the view.
     */
    @FXML
    public void initialize() {
        initListView();
        visualizationProgressBar.progressProperty().bindBidirectional(progress);
    }

    /**
     * For first time program is launch, install the virtual environment for python
     */
    private boolean firstTime;

    /**
     * Initializes the context menus for each listView item.
     */
    private void initListView() {
        listSentences.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>();

            ContextMenu contextMenu = new ContextMenu();

            MenuItem deleteItem = new MenuItem("Delete sentence");
            deleteItem.setOnAction(event -> listSentencesItems.remove(
                    listSentences.getSelectionModel().getSelectedIndex()
            ));

            contextMenu.getItems().add(deleteItem);

            cell.textProperty().bind(cell.itemProperty());

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell;
        });
    }

    /**
     * Adds the sentence in the textField to the list.
     */
    public void addSentence() {
        if (sentenceField.getText() != null && !Objects.equals(sentenceField.getText(), "")) {
            listSentencesItems.add(sentenceField.getText());
            listSentences.setItems(listSentencesItems);
            sentenceField.setText("");
        }
    }

    /**
     * Opens a window for consulting results.
     */
    private void openResultsWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/output.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(Tools.windowTitleBase);
            stage.setScene(new Scene(root));
            Stageable controller = loader.getController();
            stage.setMinWidth(Tools.windowSize[0].doubleValue());
            stage.setMinHeight(Tools.windowSize[1].doubleValue());
            stage.show();
            controller.initStage(stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Launches processing of the sentences in the listView.
     */
    public void visualize() {
        writeTxt();
        progress.set(0.25);
        if (!isWindows) {
            launchScript();
            Main.xmlSemanticsFile = new File("../sentences.sem.xml");
            openResultsWindow();
        } else {
            progress.set(0.0);
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setContentText("Windows isn't available yet");
            a.showAndWait();
        }
    }

    /**
     * Writes the sentences to the sentences.txt file.
     */
    private void writeTxt() {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("../sentences.txt"), "utf-8"))) {

            //Browse the list and write each items to "sentences.txt"
            for (String s : listSentencesItems) {
                writer.write(s);

                //add a dot if there isn't
                char[] sentenceChar = s.toCharArray();
                if (sentenceChar[sentenceChar.length - 1] != '.') {
                    writer.write(".");
                }

                //next line
                ((BufferedWriter) writer).newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Launches the python scripts, using ccg2lambda.
     */
    private void launchScript() {
        //script
        System.out.println(System.getProperty("os.name"));

        String ccg2lambdaPath = Main.ccg2lambdaLocation.getAbsolutePath();
        Process process;

        try {
            System.out.println("tokenize");
            process = new ProcessBuilder("./src/visualization/scripts/tokenize.sh", ccg2lambdaPath).start();
            progress.set(0.50);
            process.waitFor();

            System.out.println("ccgParser");
            process = new ProcessBuilder("./src/visualization/scripts/ccgParse.sh", ccg2lambdaPath).start();
            progress.set(0.75);
            process.waitFor();

            File f = new File("py3");
            firstTime = !f.exists() && !f.isDirectory();

            if (firstTime) {
                System.out.println("python virtual");
                process = new ProcessBuilder("./src/visualization/scripts/pythonVirtual.sh").start();
                process.waitFor();
                firstTime = false;
            }


            System.out.println("python script");
            process = new ProcessBuilder("./src/visualization/scripts/pythonScripts.sh", ccg2lambdaPath).start();
            progress.set(1.00);
            process.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fires when the return key is pressed.
     *
     * @param ae the event triggered by this action
     */
    public void enterPressed(ActionEvent ae) {
        addSentence();
    }

    /**
     * See the information about the software
     */
    public void displayInformation() {
        Alert popupInfo = new Alert(Alert.AlertType.INFORMATION);
        popupInfo.setTitle("About");
        popupInfo.setHeaderText("About this software ");
        popupInfo.setContentText("This software has been created by Gaétan BASILE, Thomas GUESDON and Nathan JOUBERT for the Bekki Lab at the Ochanomizu University" + "\n"
                + "Using ccg2lambda created by Pascual MARTINEZ-GOMEZ, Koji MINESHIMA, Yusuke MIYAO and Daisuke BEKKI, " + "\n"
                + "a tool to derive formal semantic representations of natural language sentences given CCG derivation trees and semantic templates.");
        popupInfo.getDialogPane().setMinWidth(1000);
        popupInfo.getDialogPane().setMinHeight(100);
        popupInfo.showAndWait();
    }

    /**
     * Redirect to the displayReadme
     */
    public void displayReadme() {
        String url = "https://github.com/mynlp/ccg2lambda#ccg2lambda-composing-semantic-representations-guided-by-ccg-derivations";
        //new ProcessBuilder("x-www-browser", url).start();
        Main.openLink(url);
    }

    public void setccg2lambdaLocation() {
        DirectoryChooser locationChooser = new DirectoryChooser();
        locationChooser.setTitle("select ccg2lambda installation directory");
        File selected = locationChooser.showDialog(view);
        if (selected != null)
            if (selected.exists() && selected.isDirectory()) {
                if (selected.canRead() && selected.canExecute() && selected.canWrite())
                    Main.ccg2lambdaLocation = selected;
            }
        System.out.println(Main.ccg2lambdaLocation);
    }

    @Override
    public void initStage(Stage primaryStage) {
        this.view = primaryStage;
    }
}
