package visualization.graph;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.SparseGraph;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * @author Thomas Guesdon
 */
public class Graph {
    private ArrayList<Node> nodes = new ArrayList<Node>();

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public String toString() {
        String tmp = "";
        for (Node n : nodes) {
            tmp += n.toString() + "\n";
            if (!n.getLinks().isEmpty()) {
                for (Link l : n.getLinks()) {
                    System.out.println("there is a link from this node : " + l.toString());
                }
            }
        }
        return tmp;
    }

    public DirectedSparseGraph<Node,Link> graph2Jung(){
        DirectedSparseGraph<Node,Link> res = new DirectedSparseGraph<>() ;
        for(Node n : nodes){
            res.addVertex(n);
            if(!n.getLinks().isEmpty()){
                for(Link l : n.getLinks()){
                    res.addEdge(l,n,l.getDestination());
                }
            }
        }
        return res;
    }

    public Node getNodeByLabel(String label) {
        Node res = null;
        for (Node n : nodes) {
            if (n.getLabel().equals(label)) {
                res = n;
            }
        }
        return res;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

}
