package visualization.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Scanner;

public class boxController {
    @FXML
    public VBox contentContainer;
    private String formula;
    @FXML
    private TitledPane box;

    public void initData(String formula) {
        this.formula = formula;
        box.setText(formula);
        Scanner sc = new Scanner(formula);
        sc.useDelimiter("&");
        do {
            String token = sc.next();
            Text display = new Text(token);
            contentContainer.getChildren().add(display);
        } while (sc.hasNext());
    }
}
