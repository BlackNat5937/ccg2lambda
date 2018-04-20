package visualization.box;

/**
 * @author Nathan Joubert
 *
 */
public class Element {
    private String label;
    private String realName;
    private ElementType elementType;
    private LinkBox linkTo = null;

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

    public void setLabel(String label) {
        this.label = label;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public ElementType getElementType() {
        return elementType;
    }

    public void setElementType(ElementType elementType) {
        this.elementType = elementType;
    }


    public LinkBox getLinkTo() {
        return linkTo;
    }

    public void setLinkTo(LinkBox linkTo) {
        this.linkTo = linkTo;
    }
    public void addLink(Element e1, Element e2)
    {
        linkTo = new LinkBox(this, e1, e2);
    }

}
