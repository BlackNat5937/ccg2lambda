package visualization.graph;

import java.util.ArrayList;

public class Graph {
    private ArrayList<Link> links = new ArrayList<Link>();
    private ArrayList<Node> nodes = new ArrayList<Node>();

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public String toString(){
        String tmp = "";
        for(Node n : nodes){
            tmp += n.toString() + "\n";
        }
        for(Link l : links){
            tmp += l.toString() + "\n";
        }
        return tmp;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public ArrayList<Link> getLinks() {
        return links;
    }

    public void setLinks(ArrayList<Link> links) {
        this.links = links;
    }
}
