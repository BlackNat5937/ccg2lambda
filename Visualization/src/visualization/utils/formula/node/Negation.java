package visualization.utils.formula.node;

import java.util.ArrayList;

public class Negation extends BaseNode {
    /**
     * Contains a list of node that are negated
     */
    private ArrayList<FormulaNode> negated = new ArrayList<>();

    public Negation() {
        super();
    }

    public Negation(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String toString() {
        StringBuilder res = new StringBuilder("Negation : ");
        for (FormulaNode bn : negated) {
            res.append(" ").append(bn.toString());
        }
        return res.toString();
    }

    public ArrayList<FormulaNode> getNegated() {
        return negated;
    }
}
