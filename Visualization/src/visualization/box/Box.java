package visualization.box;

import java.util.ArrayList;

public class Box {
    private ArrayList<Element> elements = new ArrayList<Element>();


    public ArrayList<Element> getElements() {
        return elements;
    }

    public void setElements(ArrayList<Element> elements) {
        this.elements = elements;
    }

    public String toString() {
        String title = "";
        String tmp = "";
        for (Element eTitle : elements) {
            title += eTitle.getLabel() + " ";
        }

        for (Element e : elements) {
            tmp += e.toString() + "\n";

            if (e.getLinkTo() != null) {
                tmp += e.getLinkTo().toString();
            }

        }

        return title + "\n" + tmp;
    }
}
