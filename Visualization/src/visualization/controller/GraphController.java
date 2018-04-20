package visualization.controller;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import visualization.graph.Graph;
import visualization.graph.Node;
import visualization.graph.NodeType;

import java.util.regex.Pattern;

public class GraphController implements Parametrable<String>{

    @FXML
    private Canvas canvas;

    @FXML
    private TitledPane box;

    //@FXML
    //private AnchorPane content;

    public void initData(String formula){
        Graph g = new Graph();
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
                    //System.out.println(ss.charAt(i));
                    variables += ss.charAt(i);
                    i++;
                }
                System.out.println("variables :"  + variables);
                String variable[] = variables.split(",");

                Node nodeEvent = new Node(event,NodeType.EVENT);
                for(String s : variable){
                    g.getNodeByLabel(s).addLink(nodeEvent,"event");
                }
                g.getNodes().add(nodeEvent);
                System.out.println(g.toString());

            }

        }


        box.setText(formula);
        g.generateCanvas(canvas);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }
}
