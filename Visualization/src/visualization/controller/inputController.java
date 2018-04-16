package visualization.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    @FXML
    private TextField sentenceField;

    @FXML
    private ProgressBar visualizationProgressBar;

    @FXML
    private ListView<String> listSentences;

    private ObservableList<String> listSentencesItems = FXCollections.observableArrayList();

    /**
     * Initializes the view.
     */
    @FXML
    public void initialize() {
        initListView();
    }

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
     * Launches processing of the sentences in the listView.
     */
    public void visualize() {
        writeTxt();
        visualizationProgressBar.setProgress(0.05);
        launchScript();

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
     * Launches the python scripts, using ccg2lambda.
     */
    public void launchScript() {
        //script
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

        String ccg_path = "../";
        Process process;

        if (isWindows) {
            //the launch on windows will be done later
        }
        else
        {
            try {
                process = new ProcessBuilder("./src/visualization/scripts/tokenize.sh", "../").start();
                process.waitFor();
                visualizationProgressBar.setProgress(0.15);

                process = new ProcessBuilder("./src/visualization/scripts/ccgParse.sh", "../").start();
                process.waitFor();
                visualizationProgressBar.setProgress(0.35);

                process = new ProcessBuilder("./src/visualization/scripts/pythonScripts.sh", "../").start();
                process.waitFor();
                visualizationProgressBar.setProgress(1);


            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
}
