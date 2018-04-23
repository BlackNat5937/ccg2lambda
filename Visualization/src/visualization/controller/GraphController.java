package visualization.controller;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
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
import java.util.regex.Pattern;

public class GraphController implements Parametrable<String>{

    @FXML
    private TitledPane box;

    @FXML
    private Pane testPane;

    private Formula formula;

    public void initData(String formula){
        Graph g = new Graph();

        this.formula = Formula.parse(formula);

        for(Actor actor : this.formula.getActors().values()){
            Node a = new Node(actor.getName(),NodeType.ACTOR);
            Node x = new Node(actor.getId(),NodeType.ACTOR);
            x.addLink(a, "is-a");
            g.getNodes().add(a);
            g.getNodes().add(x);
        }
        for (Event event : this.formula.getEvents().values()) {
            Node e = new Node(event.getName(),NodeType.EVENT);
            Node x = new Node(event.getId(),NodeType.EVENT);
            x.addLink(e, "is-a");

            g.getNodes().add(x);
            g.getNodes().add(e);

            for(Actor a : event.getActors()){
                g.getNodeByLabel(a.getId()).addLink(x,"event");
            }
        }
        for (Conjunction conjunction : this.formula.getConjunctions().values()) {
            Node c = new Node(conjunction.getName(),NodeType.CONJUNCTION);
            g.getNodes().add(c);

            for(FormulaNode f : conjunction.getJoined()){
                if(f == conjunction.getJoined().get(conjunction.getJoined().size() - 1)){
                    c.addLink(g.getNodeByLabel(f.getId()),"conj");
                }
                else{
                    g.getNodeByLabel(f.getId()).addLink(c,"conj");
                }

            }
        }
        box.setText(formula);

        addGraph(g);

    }

    public void addGraph(Graph g){
        DirectedSparseGraph<Node,Link> jungGraph = g.graph2Jung();
        FRLayout<Node,Link> layout = new FRLayout<>(jungGraph);
        layout.setSize(new Dimension(500,500));

        BasicVisualizationServer<Node,Link> vv = new BasicVisualizationServer<Node, Link>(layout);
        //links text
        vv.getRenderContext().setEdgeLabelTransformer(new Transformer<Link, String>() {
            @Override
            public String transform(Link link) {
                return link.getText();
            }
        });
        vv.getRenderContext().setEdgeLabelClosenessTransformer(new Transformer<Context<edu.uci.ics.jung.graph.Graph<Node, Link>, Link>, Number>() {
            @Override
            public Number transform(Context<edu.uci.ics.jung.graph.Graph<Node, Link>, Link> graphLinkContext) {
                return 35;
            }
        });
        //node text
        vv.getRenderContext().setVertexLabelTransformer(new Transformer<Node, String>() {
            @Override
            public String transform(Node node) {
                return node.getLabel();
            }
        });
        //node color
        vv.getRenderContext().setVertexFillPaintTransformer(new Transformer<Node, Paint>() {
            @Override
            public Paint transform(Node node) {
                Paint p = null;
                switch (node.getNodeType()){
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
                switch (link.getDestination().getNodeType()){
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
                switch (link.getDestination().getNodeType()){
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

        testPane.getChildren().add(sn);
    }
}
