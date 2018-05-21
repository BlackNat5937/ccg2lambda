package visualization.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
        } else if (data instanceof Disjunction) {

            container.setContent(createContentDisjunction((Disjunction) data));
            box.setDisable(true);
            container.setText(setHeaderDisjunction((Disjunction) data));
        } else if (data instanceof BaseNode) {
            container.setContent(createContentDisjunctionBox((BaseNode) data));
            box.setDisable(true);
        }
    }


    /**
     * Creates the content of the box by using the formula.
     */
    private void createContent() {
        for (Actor actor : formula.getActors().values()) {
            boxContent.add(actor.toString());
            for(BaseNode equals : actor.getEqualities()){
                boxContent.add(actor.toString()+ " = " + equals.toString());
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
            for (FormulaNode bn : negation.getNegated()) {
                if (bn.getClass() == Event.class) {
                    boxContent.remove(bn.getName() + "(" + bn.getId() + ")");
                }
                boxContent.remove(bn.toString());
            }
        }

        for (Disjunction disjunction : this.formula.getDisjunctions()) {
            boxContent.remove(disjunction.getArg1().toString());
            boxContent.remove(disjunction.getArg2().toString());
            if (disjunction.getArg1().getClass() == Conjunction.class) {
                if (disjunction.getArg1().toString().contains(",")) {
                    String id = disjunction.getArg1().toString().split(",")[1].split("\\)")[0];
                    Conjunction conj = (Conjunction) disjunction.getArg1();
                    for (FormulaNode fn : conj.getJoined()) {
                        if (id.equals(fn.getId())) {
                            boxContent.remove(fn.toString());
                        }
                    }
                }
            }
            if (disjunction.getArg2().getClass() == Conjunction.class) {
                if (disjunction.getArg2().toString().contains(",")) {
                    String id = disjunction.getArg2().toString().split(",")[1].split("\\)")[0];
                    Conjunction conj = (Conjunction) disjunction.getArg2();
                    for (FormulaNode fn : conj.getJoined()) {
                        if (id.equals(fn.getId())) {
                            boxContent.remove(fn.toString());
                        }
                    }
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
            hBoxNeg.getChildren().add(getLoadedPane(negation, "visualization/view/box.fxml"));
            contentContainer.getChildren().add(hBoxNeg);
        }

        for (Disjunction disjunction : this.formula.getDisjunctions()) {

            System.out.println("in disjunction for");
            System.out.println("disjunction :" + disjunction);

            HBox hBoxDisj = new HBox(15);


            hBoxDisj.getChildren().add(getLoadedPane(disjunction, "visualization/view/box.fxml"));

            contentContainer.getChildren().add(hBoxDisj);
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
     * @author Nathan Joubert
     */
    private TitledPane getLoadedPane(Object other, String viewPath) {
        TitledPane negationPane = null;
        Parametrable<Object> objectParametrable;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(viewPath));

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
     * @author Nathan Joubert
     */
    private Node createContentNegation(Negation negation) {
        VBox vBoxNeg = new VBox();
        for (FormulaNode bn : negation.getNegated()) {
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
     * @author Nathan Joubert
     */
    private String setHeaderNegation(Negation negation) {
        StringBuilder headerNeg = new StringBuilder();
        for (FormulaNode bn : negation.getNegated()) {
            headerNeg.append(bn.getId()).append(" ");
        }
        return headerNeg.toString();
    }

    private Node createContentDisjunction(Disjunction disjunction) {

        System.out.println("in createContentDisjunction : " + disjunction);
        System.out.println("disjunction origin " + disjunction.getOrigin().toString());
        System.out.println("disjunction arg1 " + disjunction.getArg1().toString());
        System.out.println("disjunction arg2 " + disjunction.getArg2().toString());

        HBox HBOX = new HBox(15);
        Text disjText = new Text("∨");


        HBOX.getChildren().add(getLoadedPane(disjunction.getArg1(), "visualization/view/box.fxml"));

        HBOX.getChildren().add(disjText);

        HBOX.getChildren().add(getLoadedPane(disjunction.getArg2(), "visualization/view/box.fxml"));


        return HBOX;
    }

    private Node createContentDisjunctionBox(BaseNode disjArg) {

        VBox vBoxDisj = new VBox();
        vBoxDisj.getChildren().add(new Text(disjArg.toString()));

        if (disjArg.getClass() == Conjunction.class) {
            if (disjArg.toString().contains(",")) {
                String id = disjArg.toString().split(",")[1].split("\\)")[0];
                Conjunction conj = (Conjunction) disjArg;
                for (FormulaNode fn : conj.getJoined()) {
                    if (id.equals(fn.getId())) {
                        vBoxDisj.getChildren().add(new Text(fn.toString()));
                    }
                }
            }
        }


        return vBoxDisj;
    }

    private String setHeaderDisjunction(Disjunction disjunction) {
        StringBuilder headerDisj = new StringBuilder();
        for (BaseNode bn : disjunction.getEqualities()) {
            headerDisj.append(bn.getId()).append(" ");
        }
        return headerDisj.toString();
    }
}
