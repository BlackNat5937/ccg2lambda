package visualization.utils.formula;

import java.util.Scanner;

/**
 * This interface describes basic functionnality for a {@link Formula} parser.
 *
 * @author Gaétan Basile
 */
public interface FormulaParser {
    /**
     * Parses a lambda.
     *
     * @param lambda   the lambda to parse
     * @param sentence the sentence lambda was created from
     * @return a {@link Formula} containing all the tokens of the lambda in ordered collections
     */
    Formula parse(String lambda, String sentence);

    /**
     * Simplifies the lambda. Replaces some chars and deletes tautologies.
     *
     * @param lambda the lambda to simplify
     * @return the simplified lambda
     */
    static String simplifyLambda(String lambda) {
        String correctedLambda = lambda.replace("&amp;", "&");
        StringBuilder simpLambda = new StringBuilder();
        Scanner sc = new Scanner(correctedLambda);
        sc.useDelimiter("\\s*& TrueP\\s*|\\s*TrueP &\\s*|\\s*& True\\s*|\\s*True &\\s*");
        String part;
        do {
            part = sc.next();
            simpLambda.append(' ').append(part);
        } while (sc.hasNext());
        return simpLambda.toString().trim();
    }
}
