package visualization.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Controller for the view of a formula in DRS notation.
 *
 * @author Gaétan Basile
 * @see visualization.view
 * @see <a href="https://en.wikipedia.org/w/index.php?title=Discourse_representation_structure">DRS details</a>
 */
public class BoxController implements Parametrable<String> {
    /**
     * Pattern to match the declarations of variables.
     */
    private static final Pattern boxTokenMatcher =
            Pattern.compile("\\(\\w+ = _\\)|_\\w+\\(((\\w+)|(\\w+,\\w+))\\)");
    /**
     * Pattern to match the declarations of variables including proper nouns.
     */
    private static final Pattern boxTokenMatcherBis =
            Pattern.compile("\\(\\w+ = (_\\w+)\\)|_\\w+\\(((_\\w+)|(_\\w+,\\w+)|(\\w+,_\\w+))\\)");
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
    private String formula = "";
    /**
     * The header of the box.
     */
    private String header = "";
    /**
     * The content of the box, line by line.
     */
    private List<String> tokens = new ArrayList<>();

    /**
     * Initializes the data for this box. Parses the formula, and creates content and header.
     *
     * @param formula the string of the formula
     */
    public void initData(String formula) {
        this.formula = formula;
        parseFormula();

        createContent();
        createHeader();
    }

    /**
     * Parses the formula and creates the tokens.
     */
    private void parseFormula() {
        Scanner sc = new Scanner(formula);
        Scanner sc2 = new Scanner(formula);
        sc.useDelimiter("&");
        sc2.useDelimiter("&");
        do {
            String token, tokenBis;
            token = sc.findInLine(boxTokenMatcher);
            tokenBis = sc2.findInLine(boxTokenMatcherBis);
            if (token != null)
                tokens.add(token);
            if (tokenBis != null)
                tokens.add(tokenBis);
            sc.next();
            sc2.next();
        } while (sc.hasNext() && sc.hasNext());
    }

    /**
     * Creates the content of the box by using the tokens.
     */
    private void createContent() {
        List<String> variables = new ArrayList<>();
        for (String token : tokens) {
            if (token.length() > 1 && token.charAt(0) == '_')
                token = token.substring(1);
            Text display = new Text(token);
            contentContainer.getChildren().add(display);
        }
    }

    /**
     * Creates the header of the box by using the tokens.
     */
    private void createHeader() {
        Pattern variableName = Pattern.compile("\\(\\w+\\)");
        StringBuilder sb = new StringBuilder(header);
        List<String> used = new ArrayList<>();
        for (String token : tokens) {
            if (variableName.matcher(token).find()) {
                sb.append(token);
                used.add(token);
            }
            sb.append(1);
        }
        header = sb.toString();
        box.setText(header);
    }
}
