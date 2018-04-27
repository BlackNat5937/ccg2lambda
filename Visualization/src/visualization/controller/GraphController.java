package visualization.controller;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import org.apache.commons.collections15.Transformer;
import visualization.graph.Graph;
import visualization.graph.Link;
import visualization.graph.Node;
import visualization.graph.NodeType;
import visualization.utils.formula.Formula;
import visualization.utils.formula.node.*;
import visualization.utils.formula.node.Event;

import java.awt.*;
import java.awt.geom.*;
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

    private EventHandler<MouseEvent> pressedMouse = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            for(Node n : layout.getGraph().getVertices()){
                Point2D pos = layout.transform(n);
                if((event.getX() > pos.getX() - 10) && (event.getX() < pos.getX() + 10)
                        && (event.getY() > pos.getY() - 10) && (event.getY() < pos.getY() +10)){
                    selected = n;
                }
            }
        }
    };

    private EventHandler<MouseEvent> draggedMouse = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if(event.getX() > 0 && event.getX() < layout.getSize().width && event.getY() > 0 && event.getY() < layout.getSize().height)
            {
                layout.setLocation(selected, new Point2D.Double(event.getX(),event.getY()));
                vv.repaint();
                if(negatedZones.containsKey(selected)){
                    negatedZones.get(selected).setCenterX(layout.transform(selected).getX());
                    negatedZones.get(selected).setCenterY(layout.transform(selected).getY());
                }
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
            g.getNodes().add(c);

            for (FormulaNode f : conjunction.getJoined()) {
                g.getNodeByLabel(f.getId()).addLink(c, "conj");
            }
        }
        for(Negation negation : this.formula.getNegations()){
            visualization.graph.Negation neg = new visualization.graph.Negation();

            for(BaseNode bn : negation.getNegated()){
                neg.getNegated().add(g.getNodeByLabel(bn.getName()));
                if(bn.getClass() == Actor.class || bn.getClass() == Event.class){
                    neg.getNegated().add(g.getNodeByLabel(bn.getId()));
                }
            }
            if(neg != null){
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
            sn.setOnMouseDragged(draggedMouse) ;
                sn.setOnMouseReleased(releasedMouse);

        container.getChildren().add(sn);

        for(visualization.graph.Negation negation : g.getNegations()){
            for(Node negated : negation.getNegated()){
                Point2D pos = layout.transform(negated);
                javafx.scene.paint.Paint p = javafx.scene.paint.Color.rgb(255,50,0);
                Circle negatedZone = new Circle(pos.getX(),pos.getY(),25.0,p);
                negatedZone.setOpacity(0.3);
                negatedZone.setOnMousePressed(pressedMouse);
                negatedZone.setOnMouseDragged(draggedMouse);
                negatedZone.setOnMouseReleased(releasedMouse);
                negatedZones.put(negated, negatedZone);
                container.getChildren().add(negatedZone);
            }
        }

        sp.setContent(container);

        //negation zone
        /*javafx.scene.shape.Polygon negationZone = new javafx.scene.shape.Polygon();
        javafx.scene.paint.Paint p = javafx.scene.paint.Color.rgb(255,50,0);



        testPane.getChildren().add(negationZone); */
    }


}
