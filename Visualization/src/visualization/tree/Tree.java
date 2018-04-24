package visualization.tree;

import java.util.ArrayList;

public class Tree {

    private String label;
    private ArrayList<Tree> children = new ArrayList<>();

    public Tree(String label) {
        this.label = label;
    }

    public String toString(){
        String res = "";

        res += "Node : " + label;
        res += "\n";

        for (Tree child : children) {
            res += child.toString();
        }

        return res;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ArrayList<Tree> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Tree> children) {
        this.children = children;
    }
}
