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
     * The subject of this event.
     */
    private Actor subject;
    /**
     * The object of this event.
     */
    private Actor object;
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
     * Get the actors for this event.
     *
     * @return a List of the actors taking part in this event
     */
    public List<Actor> getActors() {
        return actors;
    }

    /**
     * Get the subject of this event.
     *
     * @return a {@link Actor} which is the subject of this event
     */
    public Actor getSubject() {
        return subject;
    }

    /**
     * Set the subject of this event.
     *
     * @param subject the subject to set
     */
    public void setSubject(Actor subject) {
        this.subject = subject;
    }

    /**
     * Get the object of this event.
     *
     * @return a {@link Actor} which is the object of this event
     */
    public Actor getObject() {
        return object;
    }

    /**
     * Set the object of this event.
     *
     * @param object the object to set
     */
    public void setObject(Actor object) {
        this.object = object;
    }

    /**
     * Converts an event to a String.
     *
     * @return a String containing a representation similar to the one found in the lambda.
     */
    @Override
    public String toString() {
        StringBuilder actorL = new StringBuilder();
        if (actors.size() > 0) {
            for (Actor actor : actors) {
                actorL.append(actor.getId()).append(',');
            }
            // remove the last semicolon
            actorL.setLength(actorL.length() - 1);
        }

        return name + '(' + actorL + ")";
    }
}
