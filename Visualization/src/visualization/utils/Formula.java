package visualization.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a formula output by ccg2lambda.
 * @author Gaétan Basile
 */
public class Formula {
    /**
     * The pattern for recognizing a variable name.
     */
    private static final Pattern varNamePattern = Pattern.compile("_\\w+");
    /**
     * The pattern for recognizing a variable id.
     */
    private static final Pattern varIdPattern = Pattern.compile("(\\w+\\.)|(\\w+,\\w+)");

    /**
     * The actors of this formula. They are the ones to initiate or to endure events.
     */
    private Map<String, String> actors = new HashMap<>();
    /**
     * The events of this formula. They represent actions and have effect on the actors.
     */
    private Map<String, String> events = new HashMap<>();
    /**
     * The conjunctions of this formula. They link other items or provide additionnal info (time, place...)
     */
    private Map<String, String> conjunctions = new HashMap<>();

    /**
     * Private constructor for a formula, they must be created using the static parse method.
     * See {@link #parse(String)}
     */
    private Formula() {

    }

    /**
     * Parses the formula and creates the actors and the events.
     */
    public static Formula parse(String formula) {
        Formula newFormula = new Formula();

        String token;
        String varId;
        String varName;

        int eventNumber = 0;

        Scanner sc = new Scanner(formula);
        sc.useDelimiter("&");
        do {
            token = sc.next();
            // if the token is a &, do not do anything
            if ("&".equals(token)) {
            }
            // if the token is an actor declaration, add it to the actors map
            else if (token.matches(".*exists \\w+\\..*")) {
                Matcher m = varIdPattern.matcher(token);
                Matcher n = varNamePattern.matcher(token);
                if (m.find() && n.find()) {
                    varId = m.group();
                    varId = varId.substring(0, varId.length() - 1);
                    varName = n.group().substring(1);

                    newFormula.actors.put(varId, varName);
                }
            }
            // if the token is an event, add it to the events map
            else if (token.matches(".*Prog\\(.*")) {
                Matcher m = varNamePattern.matcher(token);
                if (m.find()) {
                    varName = m.group().substring(1);
                    varId = "e" + eventNumber;
                    eventNumber++;

                    newFormula.events.put(varId, varName);
                }
            }
            // if it is not anything else, then it is a conjunction, we need to add it to the conjunctions map
            else {
                Matcher m = varNamePattern.matcher(token);
                Matcher n = varIdPattern.matcher(token);
                if (m.find() && n.find()) {
                    varName = m.group().substring(1);
                    varId = n.group();

                    newFormula.conjunctions.put(varId, varName);
                }
            }
        } while (sc.hasNext());
        return newFormula;
    }

    /**
     * Get the actors of the formula.
     *
     * @return a map containing all the actors by id->value
     */
    public Map<String, String> getActors() {
        return actors;
    }

    /**
     * Get the events of the formula.
     *
     * @return a map containing all the events by actor_executing_the_event->value
     */
    public Map<String, String> getEvents() {
        return events;
    }

    /**
     * Get the conjunctions of the formula.
     *
     * @return a map containing all conjunctions by actors_and_events_whom_this_applies->value
     */
    public Map<String, String> getConjunctions() {
        return conjunctions;
    }
}
