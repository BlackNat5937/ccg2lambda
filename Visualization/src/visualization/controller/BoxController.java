package visualization.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import visualization.utils.Formula;

/**
 * Controller for the view of a formula in DRS notation.
 *
 * @author Gaétan Basile
 * @see visualization.view
 * @see <a href="https://en.wikipedia.org/w/index.php?title=Discourse_representation_structure">DRS details</a>
 */
public class BoxController implements Parametrable<String> {
    /**
     * Container for the content of the box.
     */
    @FXML
    public VBox contentContainer;
    /**
     * Box structure. Contains the title.
     */
    @FXML
    private TitledPane box;
    /**
     * The formula output by ccg2lambda.
     */
    private Formula formula;
    /**
     * The header of the box.
     */
    private String header = "";

    /**
     * Initializes the data for this box. Parses the formula, and creates content and header.
     *
     * @param formula the string of the formula
     */
    public void initData(String formula) {
        this.formula = Formula.parse(formula);

        createContent();
        createHeader();
    }

    /**
     * Creates the content of the box by using the tokens.
     */
    private void createContent() {
    }

    /**
     * Creates the header of the box by using the tokens.
     */
    private void createHeader() {
    }
}
