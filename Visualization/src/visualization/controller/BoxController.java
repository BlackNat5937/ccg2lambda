package visualization.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import visualization.utils.formula.Formula;
import visualization.utils.formula.node.Actor;
import visualization.utils.formula.node.Conjunction;
import visualization.utils.formula.node.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the view of a formula in DRS notation.
 *
 * @author Gaétan Basile
 * @see visualization.view
 * @see <a href="https://en.wikipedia.org/w/index.php?title=Discourse_representation_structure">DRS details</a>
 */
public class BoxController implements Parametrable<String> {
    /**
     * Container box.
     */
    @FXML
    public TitledPane cont;
    /**
     * Main box structure. Contains the title.
     */
    @FXML
    private TitledPane box;
    /**
     * Container for the content of the box.
     */
    @FXML
    public VBox contentContainer;
    /**
     * The formula output by ccg2lambda.
     */
    private Formula formula;
    /**
     * The header of the box.
     */
    private StringBuilder header = new StringBuilder();
    /**
     * The contents of the box. One entry=one line.
     */
    private List<String> boxContent = new ArrayList<>();

    /**
     * Initializes the data for this box. Parses the data, and creates content and header.
     *
     * @param data the data needed for the initialisation. first is the lambda, second is the base sentence
     */
    public void initData(String... data) {
        if (data.length < 2)
            throw new IllegalArgumentException("2 arguments are needed : the lambda and the base sentence.");
        this.formula = Formula.parse(data[0], data[1]);

        createContent();
        createHeader();
        box.setDisable(true);
        cont.setText(formula.getLambda());
    }

    /**
     * Creates the content of the box by using the formula.
     */
    private void createContent() {
        for (Actor actor : formula.getActors().values()) {
            boxContent.add(actor.toString());
        }
        for (Event event : formula.getEvents().values()) {
            boxContent.add(event.getName() + '(' + event.getId() + ')');
            boxContent.add(event.toString().substring(5, event.toString().length() - 1));
        }
        for (Conjunction conjunction : formula.getConjunctions().values()) {
            boxContent.add(conjunction.toString());
        }
        for (String s : boxContent) {
            Text display = new Text(s);
            contentContainer.getChildren().add(display);
        }
    }

    /**
     * Creates the header of the box by using the formula.
     */
    private void createHeader() {
        for (String s : formula.getActors().keySet()) {
            header.append(s).append(' ');
        }
        for (String s : formula.getEvents().keySet()) {
            header.append(s).append(' ');
        }
        for (String s : formula.getConjunctions().keySet()) {
            header.append(s).append(' ');
        }
        box.setText(header.toString());
    }
}
