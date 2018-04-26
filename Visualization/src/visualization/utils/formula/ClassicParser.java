package visualization.utils.formula;

import visualization.utils.formula.node.Actor;
import visualization.utils.formula.node.BaseNode;
import visualization.utils.formula.node.Conjunction;
import visualization.utils.formula.node.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;

/**
 * ¨Parser for classic-template lambdas.
 *
 * @author Gaétan Basile
 */
public class ClassicParser extends BaseParser {
    ClassicParser() {
    }

    /**
     * Parses the lambda and creates the actors and the events.
     */
    @Override
    public Formula parse(String lambda, String sentence) {
        parseResult = new Formula(BaseParser.simplifyLambda(lambda), sentence);

        eventNumber = 0;
        conjunctionNumber = 0;

        String token;
        String varId;
        String varName;

        Scanner sc = new Scanner(parseResult.getLambda());
        sc.useDelimiter("&");
        do {
            token = sc.next();
            // if the token is a &, do not do anything
            if (!"&".equals(token)) {
                // if the token is an actor declaration, add it to the actors map
                if (token.matches(varDeclaration)) {
                    Matcher m = varIdPattern.matcher(token);
                    Matcher n = varNamePattern.matcher(token);
                    if (m.find() && n.find()) {
                        varId = m.group();
                        varId = varId.substring(0, varId.length() - 1);
                        varName = n.group().substring(1);

                        parseResult.getActors().put(varId, new Actor(varId, varName));
                    }
                }
                // if the token is an event, add it to the events map
                else if (token.matches(".*Prog\\(.*")) {
                    Matcher m = varNamePattern.matcher(token);
                    Matcher n = varIdPattern.matcher(token);
                    String actorId;

                    if (m.find() && n.find()) {
                        varName = m.group().substring(1);
                        varId = "e" + eventNumber;
                        actorId = n.group();
                        actorId = actorId.substring(1, actorId.length() - 1);
                        // if the actorId starts with "_" then it is a proper noun and we have to search for it in the map
                        if (actorId.startsWith("_")) {
                            actorId = parseResult.getActorByName(actorId.substring(1));
                        }
                        eventNumber++;

                        parseResult.getEvents().put(varId, new Event(varId, varName, parseResult.getActors().get(actorId)));
                    }
                }
                // if it is not anything else, then it is a conjunction, we need to add it to the conjunctions map
                else {
                    String joinedId;
                    Matcher m = varNamePattern.matcher(token);
                    Matcher n = varIdPattern.matcher(token);

                    if (m.find() && n.find()) {
                        varName = m.group().substring(1);
                        varId = "c" + conjunctionNumber;
                        conjunctionNumber++;

                        joinedId = n.group();
                        joinedId = joinedId.substring(1, joinedId.length() - 1);
                        String[] ids = joinedId.split(",");
                        List<Actor> joined = new ArrayList<>();
                        for (String id : ids) {
                            if (!(id.startsWith("e") || id.startsWith("c"))) {
                                // if the id stats with "_" then it is a proper noun and we have to search for it in the map
                                if (id.startsWith("_")) {
                                    id = parseResult.getActorByName(id.substring(1));
                                }
                                // else we just add it to the list of actors joined
                                joined.add(parseResult.getActors().get(id));
                            }
                        }
                        parseResult.getConjunctions().put(varId, new Conjunction(varId, varName, joined.toArray(new BaseNode[0])));
                    }
                }
            }
        } while (sc.hasNext());
        return parseResult;
    }
}
