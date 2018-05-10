package visualization.controller;

import com.sun.org.apache.xpath.internal.operations.Neg;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import visualization.utils.formula.Formula;
import visualization.utils.formula.node.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the view of a formula in DRS notation.
 *
 * @author Gaétan Basile
 * @see visualization.view
 * @see <a href="https://en.wikipedia.org/w/index.php?title=Discourse_representation_structure">DRS details</a>
 */
public class BoxController implements Parametrable<Object> {
    /**
     * Container box.
     */
    @FXML
    public TitledPane container;
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
    @Override
    public void initData(Object data) {
        if (data instanceof Formula) {
            this.formula = (Formula) data;
            createContent();
            createHeader();
            box.setDisable(true);
            container.setText(formula.getLambda());
        } else if (data instanceof Negation) {
            container.setContent(createContentNegation((Negation) data));
            box.setDisable(true);
            container.setText(setHeaderNegation((Negation) data));

        }
    }


    /**
     * Creates the content of the box by using the formula.
     */
    private void createContent() {


        for (Actor actor : formula.getActors().values()) {

            //    if(formula.getNegations().contains(actor))

//            System.out.println("actor.getEqualities().toString() " + actor.getEqualities().toString());

            boxContent.add(actor.toString());
            if (!actor.getEqualities().isEmpty()) {
                boxContent.add(actor.getEqualities().toString());
            }
        }
        for (Event event : formula.getEvents().values()) {
            boxContent.add(event.getName() + '(' + event.getId() + ')');
            boxContent.add(event.toString());
        }
        for (Conjunction conjunction : formula.getConjunctions().values()) {
            boxContent.add(conjunction.toString());
        }

        for (Negation negation : this.formula.getNegations()) {
            for (BaseNode bn : negation.getNegated()) {
                if (bn.getClass() == Event.class) {
                    if (boxContent.contains(bn.getName() + "(" + bn.getId() + ")")) {
                        boxContent.remove(bn.getName() + "(" + bn.getId() + ")");
                    }
                }
                if (boxContent.contains(bn.toString())) {
                    boxContent.remove(bn.toString());
                }

            }
        }

        for (String s : boxContent) {
            Text display = new Text(s);
            contentContainer.getChildren().add(display);
        }

        for (Negation negation : this.formula.getNegations()) {
            HBox hBoxNeg = new HBox(15);
            Text negText = new Text("¬");
            negText.resize(35, 35);
            hBoxNeg.getChildren().add(negText);
            hBoxNeg.getChildren().add(getLoadedPane(negation, "../view/box.fxml"));
            contentContainer.getChildren().add(hBoxNeg);

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

    /**
     * For creating the new VBox (for negation and disjonction for instance)
     *
     * @param other
     * @param viewPath
     * @return
     */
    private TitledPane getLoadedPane(Object other, String viewPath) {
        TitledPane negationPane = null;
        Parametrable<Object> objectParametrable;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(viewPath));

        try {
            negationPane = fxmlLoader.load();
            objectParametrable = fxmlLoader.getController();
            objectParametrable.initData(other);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return negationPane;
    }


    /**
     * set the content of the VBox Negated
     *
     * @param negation
     * @return
     */
    private Node createContentNegation(Negation negation) {
        VBox vBoxNeg = new VBox();
        for (BaseNode bn : negation.getNegated()) {
            if (bn.getClass() == Event.class) {
                vBoxNeg.getChildren().add(new Text(bn.getName() + "(" + bn.getId() + ")"));
                vBoxNeg.getChildren().add(new Text(bn.toString()));
            } else {
                vBoxNeg.getChildren().add(new Text(bn.toString()));
            }
        }
        return vBoxNeg;
    }

    /**
     * Create and return the hearder for the negation
     *
     * @param negation
     * @return
     */
    private String setHeaderNegation(Negation negation) {
        String headerNeg = "";
        for (BaseNode bn : negation.getNegated()) {
            headerNeg = headerNeg + bn.getId() + " ";
        }
        return headerNeg;
    }

}
