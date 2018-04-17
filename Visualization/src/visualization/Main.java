package visualization;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

/**
 * Main entry point for the application.
 *
 * @author Nathan Joubert
 * @author Thomas Guesdon
 * @author Gaétan Basile
 */
public class Main extends Application {

    /**
     * Main method
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        if (args.length == 0)
            launch(args);
        else {
            String semanticsFilePath = args[0];
            List<String> formulas = Tools.getSemanticsFormulas(new File(args[0]));
        }
    }

    /**
     * Initializes the javaFX window and displays it.
     *
     * @param primaryStage the base window
     * @throws Exception any exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("view/input.fxml"));
        primaryStage.setTitle(Tools.windowTitleBase);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
