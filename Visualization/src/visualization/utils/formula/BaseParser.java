package visualization.utils.formula;

import java.util.Scanner;
import java.util.regex.Pattern;

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
    /**
     * The number of the current event node.
     */
    int eventNumber = 0;
    /**
     * The number of the current conjunction node.
     */
    int conjunctionNumber = 0;

    /**
     * Simplifies the lambda. Replaces some chars and deletes tautologies.
     *
     * @param lambda the lambda to simplify
     * @return the simplified lambda
     */
    public static String simplifyLambda(String lambda) {
        String correctedLambda = lambda.replace("&amp;", "&");
        StringBuilder simpLambda = new StringBuilder();
        Scanner sc = new Scanner(correctedLambda);
        sc.useDelimiter("\\s*& TrueP\\s*|\\s*TrueP &\\s*|\\s*& True\\s*|\\s*True &\\s*");
        String part;
        do {
            part = sc.next();
            simpLambda.append(' ').append(part);
        } while (sc.hasNext());
        return simpLambda.toString();
    }
}
