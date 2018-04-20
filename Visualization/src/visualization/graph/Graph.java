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


        setPosNodes(null, true, 0);


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

    private void setPosNodes(Node root, boolean first, int line) {
        int jumpY = 60;
        /**
         * for first time entering, find the root
         */
        if (first) {
            for (Node n : nodes) {

                if (root == null) {
                    root = n;
                }

                if (n.getLinks().size() > root.getLinks().size()) {
                    root = n;
                }
            }
            /**
             * set the pos of the root
             */

            System.out.println("in first, root :" + root);

            root.setX(50);
            root.setY(10);
            line++;
            root.setVisited(true);
            switch (root.getLinks().size()) {
                case 1:
                    root.getLinks().get(0).getDestination().setX(50);
                    root.getLinks().get(0).getDestination().setY(jumpY * line);
                    setPosNodes(root.getLinks().get(0).getDestination(), false, line + 1);
                    break;
                case 2:
                    root.getLinks().get(0).getDestination().setX(25);
                    root.getLinks().get(0).getDestination().setY(jumpY * line);
                    setPosNodes(root.getLinks().get(0).getDestination(), false, line + 1);

                    root.getLinks().get(1).getDestination().setX(75);
                    root.getLinks().get(1).getDestination().setY(jumpY * line);
                    setPosNodes(root.getLinks().get(1).getDestination(), false, line + 1);
                    break;
                case 3:
                    root.getLinks().get(0).getDestination().setX(15);
                    root.getLinks().get(0).getDestination().setY(jumpY * line);
                    setPosNodes(root.getLinks().get(0).getDestination(), false, line + 1);

                    root.getLinks().get(1).getDestination().setX(50);
                    root.getLinks().get(1).getDestination().setY(jumpY * line);
                    setPosNodes(root.getLinks().get(1).getDestination(), false, line + 1);

                    root.getLinks().get(2).getDestination().setX(85);
                    root.getLinks().get(2).getDestination().setY(jumpY * line);
                    setPosNodes(root.getLinks().get(2).getDestination(), false, line + 1);
                    break;
            }



        }
        /**
         * when not first line
         */
        else {
            root.setVisited(true);
            switch (root.getLinks().size()) {
                case 0:
                    System.out.println("node no branch, root :" + root);
                    break;
                case 1:
                    root.getLinks().get(0).getDestination().setX(50);
                    root.getLinks().get(0).getDestination().setY(jumpY * line);
                    setPosNodes(root.getLinks().get(0).getDestination(), false, line + 1);
                    break;
                case 2:
                    root.getLinks().get(0).getDestination().setX(25);
                    root.getLinks().get(0).getDestination().setY(jumpY * line);
                    setPosNodes(root.getLinks().get(0).getDestination(), false, line + 1);


                    root.getLinks().get(1).getDestination().setX(75);
                    root.getLinks().get(1).getDestination().setY(jumpY * line);
                    setPosNodes(root.getLinks().get(1).getDestination(), false, line + 1);
                    break;
                case 3:
                    root.getLinks().get(0).getDestination().setX(15);
                    root.getLinks().get(0).getDestination().setY(jumpY * line);
                    setPosNodes(root.getLinks().get(0).getDestination(), false, line + 1);

                    root.getLinks().get(1).getDestination().setX(50);
                    root.getLinks().get(1).getDestination().setY(jumpY * line);
                    setPosNodes(root.getLinks().get(1).getDestination(), false, line + 1);

                    root.getLinks().get(2).getDestination().setX(85);
                    root.getLinks().get(2).getDestination().setY(jumpY * line);
                    setPosNodes(root.getLinks().get(2).getDestination(), false, line + 1);
                    break;
            }

            System.out.println("in not first, root : "+ root);
        }


        for (Node n : nodes) {

           if(!n.isVisited())
           {
               System.out.println("n not visited :" + n);
               for(int i = 0; i < n.getLinks().size(); i++)
               {
                   n.setX(n.getLinks().get(i).getDestination().getX());
                    n.setY(n.getLinks().get(i).getDestination().getY()+jumpY*line);
               }
/**
 * problème avec park parce que pas lien avec lui, retravailler les liens obligatoires
 */
           }

        }



    }



/*    public void setPosNodes(Node visit, boolean first, int cpt, int cptEvent, int cptVariable) {
        Node biggest = null;
        *//**
     * for first time entering, no node is the biggest
     *//*
        if (first) {
            for (Node n : nodes) {
                *//**
     * if first time, take the first
     *//*
                if (biggest == null) {
                    biggest = n;
                }

                if (n.getLinks().size() > biggest.getLinks().size()) {
                    biggest = n;
                }
            }

            System.out.println("biggest " + cpt + " : " + biggest);

            biggest.setX(0);
            biggest.setY(20);
            biggest.setVisited(true);
            setPosNodes(biggest, false, cpt + 1, cptEvent, cptVariable);

        } else {
            *//**
     * when entering, node visited is not put again
     *//*
            for (Node n : nodes) {

                *//**
     *  if the one above, not enter
     *//*
                if (!n.equals(visit)) {
                    *//**
     * if already visited, not enter
     *//*
                    if (!n.isVisited()) {
                        */

    /**
     * if first time, take the first
     *//*
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

                System.out.println("biggest " + cpt + " : " + biggest);

                if (biggest.getNodeType() == NodeType.VARIABLE) {
                    if (cptVariable % 2 != 0) {
                        biggest.setX((cpt % 8) * 50);
                        biggest.setY((cpt % 8) * 50 - ((cptVariable % 8) * 50));
                    } else {
                        biggest.setX((cpt % 8) * 50);
                        biggest.setY((cpt % 8) * 50);
                    }

                    cptVariable++;
                    biggest.setVisited(true);
                } else if (biggest.getNodeType() == NodeType.EVENT) {

                    biggest.setX(0 + ((cptEvent % 8) * 30));
                    biggest.setY((cpt % 8) * 30);
                    cptEvent++;
                    biggest.setVisited(true);
                }


                setPosNodes(biggest, false, cpt + 1, cptEvent, cptVariable);
            }

        }

    }*/
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

    public Node getNodeByLabel(String label) {
        Node res = null;
        for (Node n : nodes) {
            if (n.getLabel().equals(label)) {
                res = n;
            }
        }
        return res;
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
