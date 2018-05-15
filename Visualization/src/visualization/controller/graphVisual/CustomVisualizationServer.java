package visualization.controller.graphVisual;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

import java.awt.*;

public class CustomVisualizationServer extends BasicVisualizationServer {
    public CustomVisualizationServer(Layout layout) {
        super(layout);
    }

    public void reload(int width, int height){
        getGraphLayout().setSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width,height));
        repaint();
    }
}
