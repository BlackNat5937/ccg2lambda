package visualization.utils.formula;

import visualization.utils.formula.node.Actor;
import visualization.utils.formula.node.Conjunction;
import visualization.utils.formula.node.Event;
import visualization.utils.formula.node.FormulaNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;

/**
 * Parser for event-template lambdas.
 *
 * @author Gaétan Basile
 */
public class EventParser extends BaseParser {
    /**
     * Pattern for the declaration of an events subject
     */
    private static final String eventSubjectDeclaration = "\\(Subj\\(\\w+\\) = \\w+\\).*";

    EventParser() {
    }

    @Override
    public Formula parse(String lambda, String sentence) {
        parseResult = new Formula(FormulaParser.simplifyLambda(lambda.trim()), sentence);

        actorNumber = 0;
        eventNumber = 0;
        conjunctionNumber = 0;

        renameDuplicateIdentifiers();

        String token;
        String varId;
        String varName;

        Scanner sc = new Scanner(parseResult.getLambda());
        sc.useDelimiter("&");
        do {
            token = sc.next();
            token = token.trim();
            if (!token.isEmpty()) {
                // if the token is a declaration of a variable,
                if (token.matches(varDeclaration)) {
                    registerVariable(token);
                }
                // or if it is a declaration of the subject of an event variable,
                else if (token.matches(eventSubjectDeclaration)) {
                    registerEventSubject(token);
                }
                // or if it anything else (a conjunction),
                else {
                    registerConjunction(token);
                }
            }
        } while (sc.hasNext());
        return parseResult;
    }

    private void registerVariable(String token) {
        String[] parts = token.split(" ");
        //if the token is indeed a variable declaration,
        if (parts[0].equals("exists") && parts.length >= 2) {
            String[] declaration = parts[1].split("\\.");

            String varId = declaration[0];
            String varName = declaration[1].substring(
                    declaration[1].indexOf("_") + 1,
                    declaration[1].length() - (1 + 2));
            //then it is either an event,
            if (declaration[0].matches("e\\d*")) {
                parseResult.getEvents().put(varId, new Event(varId, varName));
            }
            //or a standard variable.
            else {
                parseResult.getActors().put(varId, new Actor(varId, varName));
            }
        }
    }

    private void registerEventSubject(String token) {
        String[] parts = token.split("\\)");
        if (parts[0].contains("Subj") && parts.length >= 2) {
            String varId = parts[0].substring(parts[0].length() - 1);
            String subjId = parts[1].substring(parts[1].length() - 1);

            Actor subject = parseResult.getActors().get(subjId);

            Event event = parseResult.getEvents().get(varId);
            event.setSubject(subject);
            event.getActors().add(subject);
        }
    }

    private void registerConjunction(String token) {
        String[] parts = token.split("\\(");
        if (parts[0].startsWith("_") && parts.length >= 2) {
            String varId = "c" + conjunctionNumber;
            String varName = parts[0].substring(1);
            conjunctionNumber++;

            String[] joined = parts[1].split("\\)");
            joined = joined[0].split(",");

            List<FormulaNode> joinedNodes = new ArrayList<>();
            for (String s : joined) {
                FormulaNode node;
                //its an event
                if (s.startsWith("e")) node = parseResult.getEvents().get(s);
                    //its a conjunction
                else if (s.startsWith("c")) node = parseResult.getConjunctions().get(s);
                    //its an actor
                else node = parseResult.getActors().get(s);
                if (node != null)
                    joinedNodes.add(node);
            }
            parseResult.getConjunctions().put(varId,
                    new Conjunction(varId, varName, joinedNodes.toArray(new FormulaNode[0])));
        }
    }

    public void renameDuplicateIdentifiers() {
        Scanner sc = new Scanner(parseResult.getLambda());
        sc.useDelimiter("exists");
        String part;
        String identifier;
        List<String> usedIdentifiers = new ArrayList<>();
        do {
            part = sc.next();
            identifier = part.trim().split("\\.")[0];
            if (!usedIdentifiers.contains(identifier)) {
                usedIdentifiers.add(identifier);
            } else {
                int start = parseResult.getLambda().indexOf(part);
                String subLambda = parseResult.getLambda().substring(start);
                Matcher m = varIdPattern.matcher(subLambda);

                Character id = identifier.charAt(0);
                Character newId = (char) (id + 1);
                while (m.find()) {
                    String found = m.group();
                    subLambda = subLambda.replace(found, found.replace(id, newId));
                }
                parseResult.setLambda(parseResult.getLambda().replace(part, subLambda));
                usedIdentifiers.add(newId.toString());
            }
        } while (sc.hasNext());
    }
}
