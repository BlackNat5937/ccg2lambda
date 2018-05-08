package visualization.utils.formula.node;

import java.util.ArrayList;

public class Disjunction extends BaseNode {

    private BaseNode arg1;
    private BaseNode arg2;
    private BaseNode origin;

    public Disjunction(){
        super();
    }

    public String toString(){
        return origin.toString() + " -> " + arg1.toString() + " || " + arg2.toString();
    }

    public BaseNode getOrigin() {
        return origin;
    }

    public void setOrigin(BaseNode origin) {
        this.origin = origin;
    }

    public BaseNode getArg2() {
        return arg2;
    }

    public void setArg2(BaseNode arg2) {
        this.arg2 = arg2;
    }

    public BaseNode getArg1() {
        return arg1;
    }

    public void setArg1(BaseNode arg1) {
        this.arg1 = arg1;
    }
}
