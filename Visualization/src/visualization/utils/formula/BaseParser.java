package visualization.utils.formula;

import java.util.regex.Pattern;

/**
 * Base class for {@link Formula} parsers.
 *
 * @author Gaétan Basile
 */
public abstract class BaseParser implements FormulaParser {
    /**
     * The pattern for recognizing a variable name.
     */
    static final Pattern varNamePattern = Pattern.compile("_\\w+");
    /**
     * The pattern for recognizing a variable id.
     */
    static final Pattern varIdPattern = Pattern.compile("(\\w+\\.)|(\\(\\w+,\\w+\\))|(\\(\\w+\\))");
    /**
     * The pattern for a variable declaration.
     */
    static String varDeclaration = ".*exists \\w+\\..*";
    /**
     * The formula this ClassicParser returns.
     */
    Formula parseResult;
    int actorNumber = 0;
    /**
     * The number of the current event node.
     */
    int eventNumber = 0;
    /**
     * The number of the current conjunction node.
     */
    int conjunctionNumber = 0;
}
