package visualization.box;

import java.util.ArrayList;

/**
 * @author Nathan Joubert
 *
 */
public class Box {
    private ArrayList<Element> elements = new ArrayList<Element>();

    private String title;
    private ArrayList<String> body = new ArrayList<String>();

    public ArrayList<Element> getElements() {
        return elements;
    }

    public void setElements(ArrayList<Element> elements) {
        this.elements = elements;
    }

    public String toString() {
        title = "";

        for (Element eTitle : elements) {
            title += eTitle.getLabel() + " ";
        }

        for (Element e : elements) {
            body.add(e.toString() + "\n");

            if (e.getLinkTo() != null) {
                body.add(e.getLinkTo().toString());
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

    public ArrayList<String> getBody() {
        return body;
    }

    public void setBody(ArrayList<String> body) {
        this.body = body;
    }
}
