package visualization;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import visualization.utils.Tools;

import java.io.File;
import java.util.List;
import java.util.Scanner;

/**
 * Main entry point for the application.
 *
 * @author Nathan Joubert
 * @author Thomas Guesdon
 * @author Gaétan Basile
 */
public class Main extends Application {

    /**
     * Desired visualization outputs. All by default.
     */
    public static Tools.RepresentationModes visualizationMode = Tools.RepresentationModes.ALL;
    /**
     * Current mode of the application. Used to determine whether to open the graphic window or not.
     */
    private static Tools.ApplicationModes applicationMode = Tools.ApplicationModes.UI;

    /**
     * Main method
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        StringBuilder command = new StringBuilder();
        for (String arg : args) {
            command.append(arg).append(' ');
        }
        interpretArgs(command.toString());
        launch(args);
    }

    /**
     * Interprets the command-line arguments and adjusts execution mode.
     *
     * @param command the complete line of arguments
     */
    private static void interpretArgs(String command) {
        Scanner sc = new Scanner(command);
        if (!sc.hasNext()) {
            applicationMode = Tools.ApplicationModes.UI;
        } else {
            File semanticsXmlFile = new File(sc.next());
            boolean validFile = semanticsXmlFile.canRead();
            validFile = validFile && semanticsXmlFile.isFile();

            if (validFile) {
                List<String> formulas = Tools.getSemanticsFormulas(semanticsXmlFile);
                if (!sc.hasNext()) {
                    applicationMode = Tools.ApplicationModes.VIEWER;
                } else {
                    for (String option : Tools.outputModesOption) {
                        if (sc.findInLine(option).equals(option)) {
                            visualizationMode = Tools.RepresentationModes.fromString(option);
                        }
                    }
                    if (sc.findInLine(Tools.htmlOutputOption).equals(Tools.htmlOutputOption)) {
                        applicationMode = Tools.ApplicationModes.PIPELINE;
                    }
                }
            }
        }
    }

    /**
     * Initializes the stage by loading content corresponding to the current operation mode.
     *
     * @param primaryStage the stage to initialize
     * @throws java.io.IOException if a fxml view description is missing or unreadable
     */
    private void initializeStage(Stage primaryStage) throws java.io.IOException {
        Parent content = null;
        switch (applicationMode) {
            case UI:
                content = FXMLLoader.load(getClass().getResource("view/input.fxml"));
                primaryStage.setTitle(Tools.windowTitleBase);
                break;
            case VIEWER:
                content = FXMLLoader.load(getClass().getResource("view/output.fxml"));
                primaryStage.setTitle(Tools.windowTitleBase);
                break;
            case PIPELINE:
                break;
        }
        if (content != null)
            primaryStage.setScene(new Scene(content));
    }

    /**
     * Initializes the javaFX window and displays it.
     *
     * @param primaryStage the base window
     * @throws Exception any exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        initializeStage(primaryStage);
        if (applicationMode != Tools.ApplicationModes.PIPELINE)
            primaryStage.show();
    }
}
