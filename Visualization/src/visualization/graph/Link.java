package visualization.graph;

public class Link {

    private Node origin;
    private Node destination;
    private String text;

    public Link(Node origin, Node destination, String text){
        this.origin = origin;
        this.destination = destination;
        this.text = text;
    }

    public String toString(){
        return "Link " + text + " from " + origin.toString() + " to " + destination.toString();
    }

    public Node getOrigin() {
        return origin;
    }

    public void setOrigin(Node origin) {
        this.origin = origin;
    }

    public Node getDestination() {
        return destination;
    }

    public void setDestination(Node destination) {
        this.destination = destination;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
