package visualization.controller;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import org.apache.commons.collections15.Transformer;
import visualization.graph.*;
import visualization.utils.formula.Formula;
import visualization.utils.formula.node.*;
import visualization.utils.formula.node.Event;
import visualization.utils.formula.node.Negation;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;


public class GraphController implements Parametrable<Formula> {

    @FXML
    private TitledPane box;

    @FXML
    private ScrollPane sp;

    @FXML
    private Pane container;

    private Formula formula;

    private Node selected;

    private FRLayout<Node, Link> layout;

    private BasicVisualizationServer<Node, Link> vv;

    private ArrayList<javafx.geometry.Point2D> points = new ArrayList<>();


    private EventHandler<MouseEvent> pressedMouse = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            for (Node n : layout.getGraph().getVertices()) {
                Point2D pos = layout.transform(n);
                if ((event.getX() > pos.getX() - 10) && (event.getX() < pos.getX() + 10)
                        && (event.getY() > pos.getY() - 10) && (event.getY() < pos.getY() + 10)) {
                    selected = n;
                }
            }
        }
    };

    private EventHandler<MouseEvent> draggedMouse = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (event.getX() > 0 && event.getX() < layout.getSize().width && event.getY() > 0 && event.getY() < layout.getSize().height) {
                layout.setLocation(selected, new Point2D.Double(event.getX(), event.getY()));
                vv.repaint();
            }
        }
    };

    private EventHandler<MouseEvent> releasedMouse = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            selected = null;
        }
    };

    public void initData(Formula data) {
        Graph g = new Graph();

        this.formula = data;

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
            c.setId(conjunction.getId());
            g.getNodes().add(c);

            for (FormulaNode f : conjunction.getJoined()) {
                g.getNodeByLabel(f.getId()).addLink(c, "conj");
            }
        }
        for (Negation negation : this.formula.getNegations()) {
            visualization.graph.Negation neg = new visualization.graph.Negation();

            for (BaseNode bn : negation.getNegated()) {
                neg.getNegated().add(g.getNodeByLabel(bn.getName()));
                if (bn.getClass() == Actor.class || bn.getClass() == Event.class) {
                    neg.getNegated().add(g.getNodeByLabel(bn.getId()));
                }
            }

            g.getNegations().add(neg);
        }

        for(Disjunction disjunction : this.formula.getDisjunctions()){
            System.out.println(disjunction);

            Node or = new Node("OR", NodeType.DISJUNCTION);

            //origin :
            g.getNodeByLabel(disjunction.getOrigin().getId()).addLink(or, "disjunction");

            ArrayList<Link> toDelete = new ArrayList<>();

            if(disjunction.getArg1().getClass() == Event.class){
                or.addLink(g.getNodeByLabel(disjunction.getArg1().getId()), g.getNodeByLabel(disjunction.getArg1().getId()).getNodeType().toString());
                or.addLink(g.getNodeByLabel(disjunction.getArg2().getId()), g.getNodeByLabel(disjunction.getArg2().getId()).getNodeType().toString());

                //delete of useless links
                for(Link l : g.getNodeByLabel(disjunction.getOrigin().getId()).getLinks()){
                    if(l.getDestination().equals(g.getNodeByLabel(disjunction.getArg1().getId())) ||
                            l.getDestination().equals(g.getNodeByLabel(disjunction.getArg2().getId()))){
                        toDelete.add(l);
                    }
                }
                for(Link l : toDelete){
                    g.getNodeByLabel(disjunction.getOrigin().getId()).getLinks().remove(l);
                }
            }else if(disjunction.getArg1().getClass() == Conjunction.class){
                System.out.println("Conj ID : " + disjunction.getArg1().getId() + " | " + disjunction.getArg2().getId());
                or.addLink(g.getConjById(disjunction.getArg1().getId()), g.getConjById(disjunction.getArg1().getId()).getNodeType().toString());
                or.addLink(g.getConjById(disjunction.getArg2().getId()), g.getConjById(disjunction.getArg2().getId()).getNodeType().toString());
                //delete of useless links (here from origin to the conj)
                for(Link l : g.getNodeByLabel(disjunction.getOrigin().getId()).getLinks()){
                    if(l.getDestination().equals(g.getConjById(disjunction.getArg1().getId())) || l.getDestination().equals(g.getConjById(disjunction.getArg2().getId()))){
                        toDelete.add(l);
                    }
                }
                for(Link l : toDelete){
                    g.getNodeByLabel(disjunction.getOrigin().getId()).getLinks().remove(l);
                }

            }
            g.getNodes().add(or);
        }



        box.setText(formula.getLambda());

        addGraph(g);

    }

    public void addGraph(Graph g) {
        DirectedSparseGraph<Node, Link> jungGraph = g.graph2Jung();
        layout = new FRLayout<>(jungGraph);
        layout.setSize(new Dimension(600, 500));
        vv = new BasicVisualizationServer<>(layout);

        //links text
        vv.getRenderContext().setEdgeLabelTransformer(new Transformer<Link, String>() {
            @Override
            public String transform(Link link) {
                return link.getText();
            }
        });

        //node text
        /**
         * Replace events label like "ewalk1"
         */
        vv.getRenderContext().setVertexLabelTransformer(new Transformer<Node, String>() {
            @Override
            public String transform(Node node) {
                String res = node.getLabel();
                if(node.getNodeType() == NodeType.EVENT && node.getLabel().matches(".*\\d+.*") ){
                    res = node.getLabel().substring(0,1);
                }
                return res;
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
                    case DISJUNCTION:
                        s = new Ellipse2D.Double(-10,-10,25,25);
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
                    case DISJUNCTION:
                        p = Color.YELLOW;
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
                    case DISJUNCTION:
                        p = Color.YELLOW;
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

        sn.setOnMousePressed(pressedMouse);
        sn.setOnMouseDragged(draggedMouse);
        sn.setOnMouseReleased(releasedMouse);

        container.getChildren().add(sn);

        for (visualization.graph.Negation negation : g.getNegations()) {

            for (Node negated : negation.getNegated()) {
                Point2D pos = layout.transform(negated);
                points.add(new javafx.geometry.Point2D(pos.getX(), pos.getY()));
            }
            javafx.scene.shape.Polygon negationPolygon = createStartingPolygon(points);
            points.clear();
            negationPolygon.setStroke(javafx.scene.paint.Color.RED);
            negationPolygon.setStrokeWidth(5.0);
            container.getChildren().add(negationPolygon);
            container.getChildren().addAll(createControlAnchorsFor(negationPolygon.getPoints()));
        }


        sp.setContent(container);

    }

    private javafx.scene.shape.Polygon createStartingPolygon(ArrayList<javafx.geometry.Point2D> points) {
        javafx.scene.shape.Polygon p = new javafx.scene.shape.Polygon();

        for (javafx.geometry.Point2D p2d : points) {
            p.getPoints().add(p2d.getX());
            p.getPoints().add(p2d.getY());
        }

        p.setFill(javafx.scene.paint.Color.rgb(255, 50, 0));
        p.setOpacity(0.3);

        return p;
    }

    private ObservableList<Anchor> createControlAnchorsFor(final ObservableList<Double> points) {
        ObservableList<Anchor> anchors = FXCollections.observableArrayList();

        for (int i = 0; i < points.size(); i += 2) {
            final int idx = i;

            DoubleProperty xProperty = new SimpleDoubleProperty(points.get(i));
            DoubleProperty yProperty = new SimpleDoubleProperty(points.get(i + 1));

            Anchor anchor = new Anchor(getSelectedNode(xProperty.intValue(), yProperty.intValue()));

            //layout.setLocation(selected, new Point2D.Double(event.getX(), event.getY()));
            xProperty.addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    points.set(idx, (double) newValue);
                    layout.setLocation(anchor.getSelected(), xProperty.doubleValue(), yProperty.doubleValue());
                    vv.repaint();
                }
            });

            yProperty.addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    points.set(idx + 1, (double) newValue);

                    layout.setLocation(anchor.getSelected(), xProperty.doubleValue(), yProperty.doubleValue());
                    vv.repaint();
                }
            });

            anchor.init(javafx.scene.paint.Color.GOLD, xProperty, yProperty);
            if(!SimilarAnchor(anchor)){
                anchors.add(anchor);
            }
        }
        return anchors;
    }

    public boolean SimilarAnchor(Anchor anchor){
        boolean res = false;
        for(javafx.scene.Node a : container.getChildren()){
            if(a.getClass() == Anchor.class){
                Anchor castedAnchor = (Anchor) a;
                System.out.println("    --> " + castedAnchor.getSelected().getLabel() + " ?= " + anchor.getSelected().getLabel());
                if(castedAnchor.getSelected().getLabel().equals(anchor.getSelected().getLabel())){
                    res = true;
                }
            }
        }

        return res;
    }

    public Node getSelectedNode(int x, int y) {
        Node res = null;

        for (Node n : layout.getGraph().getVertices()) {
            Point2D pos = layout.transform(n);
            if ((x > pos.getX() - 25) && (x < pos.getX() + 25)
                    && (y > pos.getY() - 25) && (y < pos.getY() + 25)) {
                res = n;
            }
        }

        return res;
    }
}
