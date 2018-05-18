package visualization.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import visualization.utils.Tools;

import java.io.IOException;
import java.util.ArrayList;

public class InferencesController implements Parametrable<Object>{

    @FXML
    private HBox container;

    @FXML
    private Label result;

    /**
     *
     * @param data the data to pass -> it should be an array list of Image or DRT BOX (javafx node)
     */
    @Override
    public void initData(Object data) {
        if(data.getClass() == ArrayList.class){
            for(Object obj : (ArrayList<Object>)data){
                if(obj.getClass() == BorderPane.class){
                    container.getChildren().add((BorderPane)obj);
                }
                else if(obj.getClass() == String.class){
                    result.setText("Inference : " + obj);
                }
            }
        }
    }
}
