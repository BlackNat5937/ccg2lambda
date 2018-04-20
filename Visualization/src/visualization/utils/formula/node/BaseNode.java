package visualization.utils.formula.node;

/**
 * Implements the interface and adds some functionality.
 *
 * @author Gaétan Basile
 */
public class BaseNode implements FormulaNode {
    /**
     * The id used to recognize this node in the formula.
     */
    String id;
    /**
     * The name of the node, in general whatever word it used to be in the base sentence.
     */
    String name;

    /**
     * Get the id of this node.
     *
     * @return the is as a String
     */
    public String getId() {
        return id;
    }

    /**
     * Get the name of this node.
     *
     * @return the name as a String.
     */
    public String getName() {
        return name;
    }

    /**
     * Converts a baseNode to a String.
     *
     * @return a String containing a representation close to one found in the lambda.
     */
    @Override
    public String toString() {
        return name + '(' + id + ')';
    }
}
