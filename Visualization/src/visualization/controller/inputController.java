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
import javafx.stage.Stage;
import visualization.Tools;

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
public class inputController {

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
     * Initializes the view.
     */
    @FXML
    public void initialize() {
        initListView();
        visualizationProgressBar.progressProperty().bindBidirectional(progress);
    }

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
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../view/output.fxml")));
            Stage stage = new Stage();
            stage.setTitle(Tools.windowTitleBase);
            stage.setScene(new Scene(root));
            stage.show();
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
            Tools.xmlSemanticsFile = new File("../sentences.sem.xml");
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
    public void writeTxt() {
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Launches the python scripts, using ccg2lambda.
     */
    public void launchScript() {
        //script
        System.out.println(System.getProperty("os.name"));

        String ccg2lambdaPath = "../";
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

            if(firstTime)
            {
                System.out.println("python virtual");
                process = new ProcessBuilder("./src/visualization/scripts/pythonVirtual.sh").start();
                process.waitFor();
                firstTime = false;
            }


            System.out.println("python script");
            process = new ProcessBuilder("./src/visualization/scripts/pythonScripts.sh", ccg2lambdaPath).start();
            progress.set(1.00);
            process.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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

}
