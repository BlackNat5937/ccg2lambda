package visualization.utils.formula.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents an event in a formula.
 *
 * @author Gaétan Basile
 */
public class Event extends BaseNode {
    /**
     * The actors taking part in this event in the formula.
     */
    private List<Actor> actors = new ArrayList<>();

    /**
     * Makes an event with the base parameters.
     *
     * @param id     the id of the event
     * @param name   the name of the event
     * @param actors the list of actors taking part in the event
     */
    public Event(String id, String name, Actor... actors) {
        super();

        this.id = id;
        this.name = name;
        this.actors.addAll(Arrays.asList(actors));
    }

    /**
     * Converts an event to a String.
     *
     * @return a String containing a representation similar to the one found in the lambda.
     */
    @Override
    public String toString() {
        StringBuilder actorL = new StringBuilder();
        for (Actor actor : actors) {
            actorL.append(actor.getId()).append(',');
        }
        // remove the last semicolon
        actorL.setLength(actorL.length() - 1);

        return "Prog(" + name + '(' + actorL + "))";
    }
}
