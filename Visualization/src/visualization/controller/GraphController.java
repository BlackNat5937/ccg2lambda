package visualization.controller;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TitledPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import org.apache.commons.collections15.Transformer;
import visualization.graph.Graph;
import visualization.graph.Link;
import visualization.graph.Node;
import visualization.graph.NodeType;
import visualization.utils.formula.Formula;
import visualization.utils.formula.node.Actor;
import visualization.utils.formula.node.Conjunction;
import visualization.utils.formula.node.Event;
import visualization.utils.formula.node.FormulaNode;

import java.awt.*;
import java.awt.geom.*;



public class GraphController implements Parametrable<String> {

    @FXML
    private TitledPane box;

    @FXML
    private Pane testPane;

    private Formula formula;

    private Node selected;

    public void initData(String... data) {
        if (data.length < 2)
            throw new IllegalArgumentException("2 arguments are needed : the lambda and the base sentence.");
        Graph g = new Graph();

        this.formula = Formula.parse(data[0], data[1]);

        for (Actor actor : this.formula.getActors().values()) {
            Node a = new Node(actor.getName(), NodeType.ACTOR);
            Node x = new Node(actor.getId(), NodeType.ACTOR);
            x.addLink(a, "is-a");
            g.getNodes().add(a);
            g.getNodes().add(x);
        }
        for (Event event : this.formula.getEvents().values()) {
            Node e = new Node(event.getName(), NodeType.EVENT);
            Node x = new Node(event.getId(), NodeType.EVENT);
            x.addLink(e, "is-a");

            g.getNodes().add(x);
            g.getNodes().add(e);

            for (Actor a : event.getActors()) {
                g.getNodeByLabel(a.getId()).addLink(x, "event");
            }
        }
        for (Conjunction conjunction : this.formula.getConjunctions().values()) {
            Node c = new Node(conjunction.getName(), NodeType.CONJUNCTION);
            g.getNodes().add(c);

            for (FormulaNode f : conjunction.getJoined()) {
                g.getNodeByLabel(f.getId()).addLink(c, "conj");
            }
        }
        box.setText(data[0]);

        addGraph(g);

    }

    public void addGraph(Graph g) {
        DirectedSparseGraph<Node, Link> jungGraph = g.graph2Jung();
        FRLayout<Node, Link> layout = new FRLayout<>(jungGraph);
        layout.setSize(new Dimension(600,600));

        BasicVisualizationServer<Node, Link> vv = new BasicVisualizationServer<Node, Link>(layout);

        //links text
        vv.getRenderContext().setEdgeLabelTransformer(new Transformer<Link, String>() {
            @Override
            public String transform(Link link) {
                return link.getText();
            }
        });

        //node text
        vv.getRenderContext().setVertexLabelTransformer(new Transformer<Node, String>() {
            @Override
            public String transform(Node node) {
                return node.getLabel();
            }
        });

        vv.getRenderContext().setVertexShapeTransformer(new Transformer<Node, Shape>() {
            @Override
            public Shape transform(Node node) {
                Shape s = null;
                switch (node.getNodeType()) {
                    case ACTOR:
                        s = new Ellipse2D.Double(-10, -10, 20, 20);
                        break;
                    case EVENT:
                        s = new Rectangle(-10, -10, 20, 20);
                        break;
                    case CONJUNCTION:
                        s = new Ellipse2D.Double(-10, -10, 20, 20);
                        break;
                }
                return s;
            }
        });
        //node color
        vv.getRenderContext().setVertexFillPaintTransformer(new Transformer<Node, Paint>() {
            @Override
            public Paint transform(Node node) {
                Paint p = null;
                switch (node.getNodeType()) {
                    case ACTOR:
                        p = Color.BLUE;
                        break;
                    case EVENT:
                        p = Color.RED;
                        break;
                    case CONJUNCTION:
                        p = Color.GREEN;
                        break;
                }
                return p;
            }
        });
        //link color
        vv.getRenderContext().setEdgeDrawPaintTransformer(new Transformer<Link, Paint>() {
            @Override
            public Paint transform(Link link) {
                Paint p = null;
                switch (link.getDestination().getNodeType()) {
                    case ACTOR:
                        p = Color.BLUE;
                        break;
                    case EVENT:
                        p = Color.RED;
                        break;
                    case CONJUNCTION:
                        p = Color.GREEN;
                        break;
                }
                return p;
            }
        });

        vv.getRenderContext().setArrowFillPaintTransformer(new Transformer<Link, Paint>() {
            @Override
            public Paint transform(Link link) {
                Paint p = null;
                switch (link.getDestination().getNodeType()) {
                    case ACTOR:
                        p = Color.BLUE;
                        break;
                    case EVENT:
                        p = Color.RED;
                        break;
                    case CONJUNCTION:
                        p = Color.GREEN;
                        break;
                }
                return p;
            }
        });




        final SwingNode sn = new SwingNode();
        sn.setContent(vv);

        sn.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                for(Node n : layout.getGraph().getVertices()){
                    Point2D pos = layout.transform(n);
                    if((event.getX() > pos.getX() - 10) && (event.getX() < pos.getX() + 10)
                            && (event.getY() > pos.getY() - 10) && (event.getY() < pos.getY() +10)){
                        System.out.println(n.toString());
                        selected = n;
                    }
                }
            }
        });

        sn.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getX() > 0 && event.getX() < layout.getSize().width && event.getY() > 0 && event.getY() < layout.getSize().height)
                {
                    layout.setLocation(selected, new Point2D.Double(event.getX(),event.getY()));
                    vv.repaint();
                }
            }
        });

        /*sn.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                selected = null;
            }
        });*/


        testPane.getChildren().add(sn);


        //negation zone
        /*javafx.scene.shape.Polygon negationZone = new javafx.scene.shape.Polygon();
        javafx.scene.paint.Paint p = javafx.scene.paint.Color.rgb(255,50,0);

        for(Node n : layout.getGraph().getVertices()){
            Point2D pos = layout.transform(n);

            negationZone.getPoints().add(pos.getX());
            negationZone.getPoints().add(pos.getY());
            negationZone.setOpacity(0.3);
            negationZone.setStroke(javafx.scene.paint.Color.RED);
            negationZone.setStrokeWidth(10.0);
            negationZone.setFill(p);

        }

        testPane.getChildren().add(negationZone); */


    }



}
