package visualization.utils.formula.node;

/**
 * Represents an actor in a formula.
 *
 * @author Gaétan Basile
 */
public class Actor extends BaseNode {
    /**
     * Constructor for an actor, initializes all relevant fields.
     *
     * @param id   the id used to recognize and identify this actor in the formula.
     * @param name the name of this actor
     */
    public Actor(String id, String name) {
        super();

        this.id = id;
        this.name = name;
    }
}
