package visualization;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import visualization.controller.Stageable;
import visualization.utils.Tools;

import java.io.File;
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
     * The location of ccg2lambda.
     */
    public static File ccg2lambdaLocation;
    /**
     * The location of the CCG Parser C&C
     */
    public static File ccgCandCLocation;
    /**
     * The location of the CCG Parser easyCCg
     */
    public static File easyCCGLocation;
    /**
     * The location of the CCG Parser depccg
     */
    public static File depccgLocation;
    /**
     * Desired visualization outputs. All by default.
     */
    private static Tools.RepresentationModes visualizationMode = Tools.RepresentationModes.ALL;
    /**
     * Current mode of the application. Used to determine whether to open the graphic window or not.
     */
    public static Tools.ApplicationModes applicationMode = Tools.ApplicationModes.UI;
    /**
     * Selected template type; By default CLASSIC.
     */
    public static Tools.TemplateType selectedTemplateType = Tools.TemplateType.EVENT;
    /**
     * Selected parser type; By default only C&C.
     */
    public static Tools.ParserType selectedParserType = Tools.ParserType.CANDC;
    /**
     * The file containing the semantics representations output by ccg2lambda.
     */
    public static File xmlSemanticsFile;

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
            Main.xmlSemanticsFile = new File(sc.next());
            boolean validFile = Main.xmlSemanticsFile.canRead();
            validFile = validFile && Main.xmlSemanticsFile.isFile();

            if (validFile) {
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
        FXMLLoader loader;
        Stageable controller;
        switch (applicationMode) {
            case UI:
                loader = new FXMLLoader(getClass().getResource("view/input.fxml"));
                content = loader.load();
                controller = loader.getController();
                primaryStage.setTitle(Tools.windowTitleBase);
                controller.initStage(primaryStage);
                primaryStage.setMinWidth(400);
                primaryStage.setMinHeight(225);
                break;
            case VIEWER:
                loader = new FXMLLoader(getClass().getResource("view/output.fxml"));
                content = loader.load();
                controller = loader.getController();
                primaryStage.setTitle(Tools.windowTitleBase);
                controller.initStage(primaryStage);
                primaryStage.setMinWidth(Tools.windowSize[0].doubleValue());
                primaryStage.setMinHeight(Tools.windowSize[1].doubleValue());
                Platform.setImplicitExit(true);
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

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public static void openLink(String link) {
        new Main().getHostServices().showDocument(link);
    }
}
