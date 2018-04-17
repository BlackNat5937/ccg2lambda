package visualization.graph;

public class Node{

    private String label;
    private NodeType nodeType;

    public Node(String label, NodeType nodeType){
        this.label = label;
        this.nodeType = nodeType;
    }

    public String toString(){
        return nodeType.toString() + " " + label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }
}
