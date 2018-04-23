package visualization.controller;

import javafx.stage.Stage;

/**
 * Interface controllers can implement, which enables them to access the stage they manage.
 */
public interface Stageable {
    void initStage(Stage primaryStage);
}
