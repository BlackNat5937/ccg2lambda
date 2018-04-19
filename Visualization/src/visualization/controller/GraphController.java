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
            System.out.println(ss);

            //all variables
            if(ss.contains("exists")){
                String variable = "";
                int i = 7;
                Character tmp = ss.charAt(i);
                while(!tmp.equals('.')){
                    variable += tmp;
                    i++;
                    tmp = ss.charAt(i);
                }
                Node node = new Node(variable, NodeType.VARIABLE);
                g.getNodes().add(node);

                //add the node on which the variable point
                // exists z1.(_park(z1)) ; 3 to go grom . to p
                tmp = ss.charAt(i);
                variable = "";
                while(!tmp.equals('_')){
                    i++;
                    tmp = ss.charAt(i);
                }
                i++;
                while(Character.isLetter(tmp)){
                    variable += tmp;
                    i++;
                    tmp = ss.charAt(i);
                }


                Node node2 = new Node(variable,NodeType.VARIABLE);
                node.addLink(node2, "is-a");
                g.getNodes().add(node2);
            }

            //all events
            //Pattern pattern = Pattern.compile("_\\w+\\(");
            if(ss.matches(".*_\\w+\\(.*") && !ss.contains("exists")){

                System.out.println("EVENT :" + ss);

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
