package visualization.utils.formula.node;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a conjunction in a formula.
 *
 * @author Gaétan Basile
 */
public class Conjunction extends BaseNode {
    /**
     * The nodes which are joined by this conjunction in the formula.
     */
    private List<FormulaNode> joined;

    public Conjunction(String id, String name, FormulaNode... joined) {
        super();

        this.id = id;
        this.name = name;
        this.joined = Arrays.asList(joined);
    }

    /**
     * Converts a conjunction to a String.
     *
     * @return a String containing a representation close to one found in the lambda.
     */
    @Override
    public String toString() {
        StringBuilder nodeL = new StringBuilder();
        for (FormulaNode formulaNode : joined) {
            nodeL.append(formulaNode.getId()).append(',');
        }
        nodeL.setLength(nodeL.length() - 1);

        return name + '(' + nodeL + ')';
    }
}
