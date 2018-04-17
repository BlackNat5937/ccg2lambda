package visualization.box;

public class Element {
    private String label;
    private String realName;
    private ElementType elementType;

    public Element(String label, String realName, ElementType elementType) {
        this.label = label;
        this.realName = realName;
        this.elementType = elementType;
    }

    public String toString() {
        return realName + "(" + label + ")";
    }

    public String getLabel() {
        return label;
    }



}
