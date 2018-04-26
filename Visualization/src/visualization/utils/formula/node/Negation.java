package visualization.utils.formula.node;

import java.util.ArrayList;

public class Negation extends BaseNode {
    /**
     * Contains a list of node that are negated
     */
    private ArrayList<BaseNode> negated = new ArrayList<>();

    public Negation(){
        super();
    }

    public String toString(){
        String res = "Negation : ";
        for(BaseNode bn : negated){
            res += " " + bn.toString();
        }
        return res;
    }

    public ArrayList<BaseNode> getNegated() {
        return negated;
    }
}
