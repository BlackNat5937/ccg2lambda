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

    private Map<Node, javafx.scene.shape.Circle> negatedZones = new HashMap<>();

    private ArrayList<javafx.geometry.Point2D> points = new ArrayList<>();

    private javafx.scene.shape.Polygon negationPolygon = new javafx.scene.shape.Polygon();

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
                if (negatedZones.containsKey(selected)) {
                    negatedZones.get(selected).setCenterX(layout.transform(selected).getX());
                    negatedZones.get(selected).setCenterY(layout.transform(selected).getY());
                }
            }
        }
    };

    private EventHandler<MouseEvent> releasedMouse = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (selected != null) {

            }
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
            if (neg != null) {
                g.getNegations().add(neg);
            }

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

        sn.setOnMousePressed(pressedMouse);
        sn.setOnMouseDragged(draggedMouse);
        sn.setOnMouseReleased(releasedMouse);

        container.getChildren().add(sn);

        for (visualization.graph.Negation negation : g.getNegations()) {

            for (Node negated : negation.getNegated()) {
                Point2D pos = layout.transform(negated);
                javafx.scene.paint.Paint p = javafx.scene.paint.Color.rgb(255, 50, 0);
                Circle negatedZone = new Circle(pos.getX(), pos.getY(), 25.0, p);
                negatedZone.setOpacity(0.3);
                negatedZones.put(negated, negatedZone);

                points.add(new javafx.geometry.Point2D(pos.getX(),pos.getY()));

                //container.getChildren().add(negatedZone);
            }
        }

        negationPolygon = createStartingPolygon(points);
        container.getChildren().add(negationPolygon);
        container.getChildren().addAll(createControlAnchorsFor(negationPolygon.getPoints()));

        sp.setContent(container);

    }

    private javafx.scene.shape.Polygon createStartingPolygon(ArrayList<javafx.geometry.Point2D> points){
        javafx.scene.shape.Polygon p = new javafx.scene.shape.Polygon();

        for(javafx.geometry.Point2D p2d : points){
            p.getPoints().add(p2d.getX());
            p.getPoints().add(p2d.getY());
        }

        p.setFill(javafx.scene.paint.Color.rgb(255, 50, 0));
        p.setOpacity(0.3);

        return p;
    }

    private ObservableList<Anchor> createControlAnchorsFor(final ObservableList<Double> points){
        ObservableList<Anchor> anchors = FXCollections.observableArrayList();

        for(int i = 0; i < points.size(); i += 2){
            final int idx = i;



            DoubleProperty xProperty = new SimpleDoubleProperty(points.get(i));
            DoubleProperty yProperty = new SimpleDoubleProperty(points.get(i+1));

            Anchor anchor = new Anchor(getSelectedNode(xProperty.intValue(), yProperty.intValue()));

            //layout.setLocation(selected, new Point2D.Double(event.getX(), event.getY()));
            xProperty.addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    points.set(idx, (double) newValue);
                    layout.setLocation(anchor.getSelected(),xProperty.doubleValue(),yProperty.doubleValue());
                    vv.repaint();
                }
            });

            yProperty.addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    points.set(idx + 1, (double) newValue);

                    layout.setLocation(anchor.getSelected(),xProperty.doubleValue(),yProperty.doubleValue());
                    vv.repaint();
                }
            });

            anchor.init(javafx.scene.paint.Color.GOLD, xProperty, yProperty);
            //Anchor anchor = new Anchor(javafx.scene.paint.Color.GOLD,xProperty,yProperty);
            anchors.add(anchor);
        }
        return anchors;
    }

    public Node getSelectedNode(int x, int y){
        Node res = null;

        for(Node n : layout.getGraph().getVertices()){
            Point2D pos = layout.transform(n);
            if ((x > pos.getX() - 25) && (x < pos.getX() + 25)
                    && (y > pos.getY() - 25) && (y < pos.getY() + 25)) {
                res = n;
            }
        }

        return res;
    }
}
