package visualization.controller;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
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
        /*
        String parsedFormula[] = formula.split("&");
        for(String ss : parsedFormula){

            //all variables
            if(ss.contains("exists")){
                String variable = "";
                int i = 8;
                Character tmp = ss.charAt(i);
                while(!((Character)ss.charAt(i)).equals('.')){
                    variable += ss.charAt(i);
                    i++;
                }
                Node node = new Node(variable, NodeType.VARIABLE);
                g.getNodes().add(node);

                //add the node on which the variable point
                // exists z1.(_park(z1)) ; 3 to go grom . to p
                variable = "";
                while(!((Character)ss.charAt(i)).equals('_')){
                    i++;
                }
                i++;
                while(Character.isLetter(ss.charAt(i))){
                    variable += ss.charAt(i);
                    i++;
                }


                Node node2 = new Node(variable,NodeType.VARIABLE);
                node.addLink(node2, "is-a");
                g.getNodes().add(node2);
            }

            //all events
            //Pattern pattern = Pattern.compile("_\\w+\\(");
            if(ss.matches(".*_\\w+\\(.*") && !ss.contains("exists")){

                int i = 0;
                String event = "";
                while(!((Character)ss.charAt(i)).equals('_')){
                    i++;
                }
                i++;
                while(Character.isLetter(ss.charAt(i))){
                    event += ss.charAt(i);
                    i++;
                }
                //i is now where there is the variables which the events concern
                String variables = "";
                i++;
                while(!((Character)ss.charAt(i)).equals(')')){
                    variables += ss.charAt(i);
                    i++;
                }
                String variable[] = variables.split(",");

                Node nodeEvent = new Node(event,NodeType.EVENT);
                for(String s : variable){
                    System.out.println(s);
                    if(g.getNodeByLabel(s) != null){
                        g.getNodeByLabel(s).addLink(nodeEvent,"event");
                    }
                }
                g.getNodes().add(nodeEvent);

            }

        } */

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
                g.getNodeByLabel(f.getId()).addLink(c,"conj");
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
        //node text
        vv.getRenderContext().setVertexLabelTransformer(new Transformer<Node, String>() {
            @Override
            public String transform(Node node) {
                return node.getLabel();
            }
        });

        final SwingNode sn = new SwingNode();
        sn.setContent(vv);

        testPane.getChildren().add(sn);
    }
}
