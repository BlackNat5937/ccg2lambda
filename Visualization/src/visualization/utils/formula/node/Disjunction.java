package visualization.utils.formula.node;

import java.util.List;

public class Disjunction extends BaseNode {

    private FormulaNode arg1;
    private List<FormulaNode> arg1List;
    private FormulaNode arg2;
    private List<FormulaNode> arg2List;
    private FormulaNode origin;

    public Disjunction() {
        super();
    }

    public String toString() {
        return origin.toString() + " -> " + arg1.toString() + " || " + arg2.toString();
    }

    public FormulaNode getOrigin() {
        return origin;
    }

    public void setOrigin(FormulaNode origin) {
        this.origin = origin;
    }

    public FormulaNode getArg2() {
        return arg2;
    }

    public void setArg2(FormulaNode arg2) {
        this.arg2 = arg2;
    }

    public FormulaNode getArg1() {
        return arg1;
    }

    public void setArg1(FormulaNode arg1) {
        this.arg1 = arg1;
    }
}
