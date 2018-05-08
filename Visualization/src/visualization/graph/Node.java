package visualization.graph;

import java.util.ArrayList;

/**
 * @author Thomas Guesdon
 */
public class Node{

    private String label;
    private NodeType nodeType;
    private ArrayList<Link> links = new ArrayList<>();
    private String id;
    private boolean visited;

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

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
