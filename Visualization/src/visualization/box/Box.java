package visualization.box;

import java.util.ArrayList;

public class Box {
    private ArrayList<Element> elements = new ArrayList<Element>();

    private String title;
    private String body;

    public ArrayList<Element> getElements() {
        return elements;
    }

    public void setElements(ArrayList<Element> elements) {
        this.elements = elements;
    }

    public String toString() {
        title = "";
        body = "";
        for (Element eTitle : elements) {
            title += eTitle.getLabel() + " ";
        }

        for (Element e : elements) {
            body += e.toString() + "\n";

            if (e.getLinkTo() != null) {
                body += e.getLinkTo().toString();
            }

        }

        return title + "\n" + body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
