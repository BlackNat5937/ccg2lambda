package visualization.graph;

public class Node{

    private String label;
    private NodeType nodeType;
    private Link linkTo = null;

    public Node(String label, NodeType nodeType){
        this.label = label;
        this.nodeType = nodeType;
    }

    public Node(String label, NodeType nodeType, Node linkTo, String textLink){
        this.label = label;
        this.nodeType = nodeType;
        this.linkTo = new Link(this, linkTo, textLink);
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

    public Link getLinkTo() {
        return linkTo;
    }

    public void setLinkTo(Link linkTo) {
        this.linkTo = linkTo;
    }
}
