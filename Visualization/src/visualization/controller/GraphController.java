package visualization.controller;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import visualization.graph.Graph;
import visualization.graph.Node;
import visualization.graph.NodeType;

public class GraphController implements Parametrable<String>{

    @FXML
    private Canvas canvas;

    @FXML
    private TitledPane box;

    @FXML
    private AnchorPane content;

    public void initData(String formula){
        Graph g = new Graph();
        System.out.println(formula);
        String parsedFormula[] = formula.split("&");
        for(String ss : parsedFormula){
            System.out.println(ss);
            if(ss.contains("exists")){
                String variable = "";
                int i = 7;
                Character tmp = ss.charAt(i);
                while(!tmp.equals('.')){
                    variable += tmp;
                    i++;
                    tmp = ss.charAt(i);
                }
                System.out.println(variable);
                Node node = new Node(variable, NodeType.VARIABLE);
                g.getNodes().add(node);
            }
        }

        box.setText(formula);
        this.canvas = g.generateCanvas();
        content.getChildren().add(canvas);
        System.out.println(g.toString());
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }
}
