package visualization.box;

public class LinkBox {
    private Element verb;
    private Element subject;
    private Element noun;

    public LinkBox(Element verb, Element subject, Element noun) {
        this.verb = verb;
        this.subject = subject;
        this.noun = noun;
    }

    public String toString()
    {
        return verb.getRealName() + "(" + subject.getLabel() + ", " + noun.getLabel() + ")";
    }

    public Element getVerb() {
        return verb;
    }

    public void setVerb(Element verb) {
        this.verb = verb;
    }

    public Element getSubject() {
        return subject;
    }

    public void setSubject(Element subject) {
        this.subject = subject;
    }

    public Element getNoun() {
        return noun;
    }

    public void setNoun(Element noun) {
        this.noun = noun;
    }

}
