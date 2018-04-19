package visualization.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class boxController implements Parametrable<String> {
    @FXML
    public VBox contentContainer;
    @FXML
    private TitledPane box;

    private String formula;
    private String header;
    private List<String> elements;

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

    private List<String> parseFormula() {
        ArrayList<String> tmp = new ArrayList<>();
        Scanner sc = new Scanner(formula);
        sc.useDelimiter("&");
        do {
            String token = sc.next();
            if (token.contains("exists"))
                continue;
            else {
            }
            tmp.add(token);
        } while (sc.hasNext());
        return null;
    }
}
