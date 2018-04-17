package visualization.graph;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Graph {
    private ArrayList<Node> nodes = new ArrayList<Node>();

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    //bbbbbbbbbb
    public String toString(){
        String tmp = "";
        for(Node n : nodes){
            tmp += n.toString() + "\n";
            if(n.getLinkTo() != null){
                System.out.println("there is a link from this node : " + n.getLinkTo().toString());
            }
        }
        return tmp;
    }

    /**
     * generate a canvas with the graphic representation of the graph
     * @return canvas
     */
    public Canvas generateCanvas(){
        Canvas c = new Canvas(400,400);
        GraphicsContext gc = c.getGraphicsContext2D();
        int cpt = 0;

        for(Node n : nodes){
            n.setX((cpt % 8)*20);
            n.setY(20);
            cpt ++;
        }

        for(Node n : nodes){
            gc.setFill(Color.GREEN);
            gc.fillRect(n.getX(), n.getY(), 10,10);
            if(n.getLinkTo() != null){
                gc.setStroke(Color.BLUE);
                gc.strokeLine(n.getX(),n.getY(), n.getLinkTo().getDestination().getX(), n.getLinkTo().getDestination().getY());
            }
        }

        return c;
    }

    public int getNbNodes(){
        int cpt = 0;
        for(Node n : nodes){
            cpt ++;
        }
        return cpt;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

}
