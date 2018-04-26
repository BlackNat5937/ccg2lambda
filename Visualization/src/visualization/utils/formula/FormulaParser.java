package visualization.utils.formula;

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
}
