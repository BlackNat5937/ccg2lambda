package visualization.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
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
    private Map<String, String> tokens = new HashMap<>();

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
        Pattern varNamePattern = Pattern.compile("_\\w+");
        Pattern varIdPattern = Pattern.compile("(\\w+\\.)|(\\w+,\\w+)");

        String token;
        String varId;
        String varName;

        int eventNumber = 0;

        Scanner sc = new Scanner(formula);
        sc.useDelimiter("&");
        do {
            token = sc.next();
            if ("&".equals(token)) {
            } else if (token.matches(".*exists \\w+\\..*")) {
                Matcher m = varIdPattern.matcher(token);
                Matcher n = varNamePattern.matcher(token);
                if (m.find() && n.find()) {
                    varId = m.group();
                    varId = varId.substring(0, varId.length() - 1);
                    varName = n.group().substring(1);

                    tokens.put(varId, varName);
                }
            } else if (token.matches(".*Prog\\(.*")) {
                Matcher m = varNamePattern.matcher(token);
                if (m.find()) {
                    varName = m.group();
                    varId = "e" + eventNumber;
                    eventNumber++;

                    tokens.put(varId, varName);
                }
            }
        } while (sc.hasNext());
    }

    /**
     * Creates the content of the box by using the tokens.
     */
    private void createContent() {
        /*List<String> variables = new ArrayList<>();
        for (String token : tokens) {
            if (token.length() > 1 && token.charAt(0) == '_')
                token = token.substring(1);
            Text display = new Text(token);
            contentContainer.getChildren().add(display);
        }*/
    }

    /**
     * Creates the header of the box by using the tokens.
     */
    private void createHeader() {
        /*Pattern variableName = Pattern.compile("\\(\\w+\\)");
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
        box.setText(header);*/
    }
}
