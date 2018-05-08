package visualization.utils.formula.node;

import java.util.ArrayList;
import java.util.List;

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
     * The nodes which are "equal" to this one in a semantic sense
     */
    List<BaseNode> equalities = new ArrayList<>();

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
     * Get the equalities of this node.s
     *
     * @return the equalities as a List.
     */
    public List<BaseNode> getEqualities() {
        return equalities;
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FormulaNode))
            return false;
        else return ((FormulaNode) obj).getId().equals(this.id) || ((FormulaNode) obj).getName().equals(this.name);
    }
}
