package visualization.graph;

import java.util.ArrayList;

public class Node{

    private String label;
    private NodeType nodeType;
    private ArrayList<Link> links = new ArrayList<>();
    private int x, y;

    public Node(String label, NodeType nodeType){
        this.label = label;
        this.nodeType = nodeType;
    }

    public Node(String label, NodeType nodeType, Node linkTo, String textLink){
        this.label = label;
        this.nodeType = nodeType;
        links.add(new Link(this, linkTo, textLink));
    }

    public String toString(){
        return nodeType.toString() + " : " + label;
    }

    public void addLink(Node n, String textLink)
    {
        links.add(new Link(this, n, textLink));
    }

    public ArrayList<Link> getLinks() {
        return links;
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

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }
}
