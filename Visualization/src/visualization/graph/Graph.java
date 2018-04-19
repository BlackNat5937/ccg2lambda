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

    /**
     * generate a canvas with the graphic representation of the graph
     *
     * @return canvas
     */
    public void generateCanvas(Canvas c) {
        GraphicsContext gc = c.getGraphicsContext2D();
        int cpt = 0;


        setPosNodes(nodes.get(0), true, 0);


        /**
         * cout node
         */
        for (Node n : nodes) {

            //      n.setX((cpt % 8) * 100);
            //  n.setY(20);
            cpt++;

        }

        /**
         * fill the rects
         */
        for (Node n : nodes) {
            switch (n.getNodeType()) {
                case NOUN:
                    gc.setFill(Color.RED);
                    break;
                case EVENT:
                    gc.setFill(Color.BLUE);
                    break;
                case VARIABLE:
                    gc.setFill(Color.GREEN);
                    break;
                default:
                    gc.setFill(Color.GREEN);
                    break;
            }
            gc.fillRect(n.getX(), n.getY(), 20, 20);
            gc.setFill(Color.BLACK);
            gc.fillText(n.getLabel(), n.getX(), n.getY());

        }

        /**
         * fill the links
         */
        for (Node n : nodes) {
            if (!n.getLinks().isEmpty()) {
                gc.setStroke(Color.BLUE);
                for (Link l : n.getLinks()) {
                    drawArrow(gc, n.getX() + 10, n.getY() + 10, l.getDestination().getX() + 10, l.getDestination().getY() + 10);
                    //gc.strokeLine(n.getX() + 10,n.getY() + 10, l.getDestination().getX() + 10, l.getDestination().getY() + 10);
                    int midX = (l.getDestination().getX() + 10) - (n.getX() + 10);
                    int midY = (l.getDestination().getY() + 10) - (n.getY() + 10);
                    gc.fillText(l.getText(), n.getX() + midX / 2, n.getY() + midY / 2);
                }
            }
        }
    }

    public void setPosNodes(Node visit, boolean first, int cpt) {
        Node biggest = null;
        /**
         * for first time entering, no node is the biggest
         */
        if (first) {
            for (Node n : nodes) {
                /**
                 * if first time, take the first
                 */
                if (biggest == null) {
                    biggest = n;
                }

                if (n.getLinks().size() > biggest.getLinks().size()) {
                    biggest = n;
                }
            }
            biggest.setX(0);
            biggest.setY(20);
            biggest.setVisited(true);
            setPosNodes(biggest, false, cpt + 1);

        } else {
            /**
             * when entering, node visited is not put again
             */
            for (Node n : nodes) {

                /**
                 *  if the one above, not enter
                 */
                if (!n.equals(visit)) {
                    /**
                     * if already visited, not enter
                     */
                    if (!n.isVisited()) {
                        /**
                         * if first time, take the first
                         */
                        if (biggest == null) {
                            biggest = n;
                        }

                        if (n.getLinks().size() > biggest.getLinks().size()) {
                            biggest = n;
                        }
                    }
                }


            }

            if (!(biggest == null)) {

                biggest.setX((cpt % 8) * 50);
                biggest.setY((cpt % 8) * 50);

                biggest.setVisited(true);

                setPosNodes(biggest, false, cpt + 1);
            }

        }

    }

    public void drawArrow(GraphicsContext gc, int x1, int y1, int x2, int y2) {
        gc.strokeLine(x1, y1, x2, y2);
        int midX = x2 - x1; //distance between x2 and x1
        int midY = y2 - y1; //distance between y2 and y1
        if (midX >= 0 && midY >= 0) {
            if ((midX * midX) >= (midY * midY)) {
                gc.strokeLine(x2, y2, x2 - 5, y2 - 5);
                gc.strokeLine(x2, y2, x2 - 5, y2 + 5);
            } else {
                gc.strokeLine(x2, y2, x2 - 5, y2 - 5);
                gc.strokeLine(x2, y2, x2 + 5, y2 - 5);
            }
        } else if (midX >= 0 && midY < 0) {
            if ((midX * midX) >= (midY * midY)) {
                gc.strokeLine(x2, y2, x2 - 5, y2 - 5);
                gc.strokeLine(x2, y2, x2 - 5, y2 + 5);
            } else {
                gc.strokeLine(x2, y2, x2 - 5, y2 + 5);
                gc.strokeLine(x2, y2, x2 + 5, y2 + 5);
            }
        } else if (midX < 0 && midY <= 0) {
            if ((midX * midX) >= (midY * midY)) {
                gc.strokeLine(x2, y2, x2 + 5, y2 - 5);
                gc.strokeLine(x2, y2, x2 + 5, y2 + 5);
            } else {
                gc.strokeLine(x2, y2, x2 - 5, y2 + 5);
                gc.strokeLine(x2, y2, x2 + 5, y2 + 5);
            }
        } else if (midX < 0 && midY >= 0) {
            if ((midX * midX) >= (midY * midY)) {
                gc.strokeLine(x2, y2, x2 - 5, y2 - 5);
                gc.strokeLine(x2, y2, x2 + 5, y2 - 5);
            } else {
                gc.strokeLine(x2, y2, x2 - 5, y2 + 5);
                gc.strokeLine(x2, y2, x2 + 5, y2 + 5);
            }
        }

    }

    public int getNbNodes() {
        int cpt = 0;
        for (Node n : nodes) {
            cpt++;
        }
        return cpt;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

}
