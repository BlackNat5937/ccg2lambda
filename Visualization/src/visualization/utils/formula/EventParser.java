package visualization.utils.formula;

import visualization.utils.formula.node.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private List<String> usedIdentifiers;

    EventParser() {
    }

    @Override
    public Formula parse(String lambda, String sentence) {
        parseResult = new Formula(FormulaParser.simplifyLambda(lambda.trim()), sentence);

        actorNumber = 0;
        eventNumber = 0;
        conjunctionNumber = 0;

        renameDuplicateIdentifiers();
        //recursiveRenameDuplicateIdentifiers(parseResult.getLambda());

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
                // or if it is an equality declaration,
                else if (token.matches("\\(+\\w+ = \\w+\\)+")) {
                    registerSynonym(token);
                }
                // or if it anything else (a conjunction),
                else {
                    registerConjunction(token);
                }
            }
        } while (sc.hasNext());
        return parseResult;
    }

    private void registerSynonym(String token) {
        token = token.replace("(", "").replace(")", "");
        String[] parts = token.split("=");

        String firstS = parts[0].trim(), secondS = parts[1].trim();
        BaseNode firstN, secondN;

        firstN = parseResult.getActors().get(firstS);
        if (firstN == null)
            firstN = parseResult.getEvents().get(firstS);
        if (firstN == null)
            firstN = parseResult.getConjunctions().get(firstS);

        secondN = parseResult.getActors().get(secondS);
        if (secondN == null)
            secondN = parseResult.getEvents().get(secondS);
        if (secondN == null)
            secondN = parseResult.getConjunctions().get(secondS);

        firstN.getEqualities().add(secondN);
        secondN.getEqualities().add(firstN);
    }

    private void registerVariable(String token) {
        String[] parts = token.split(" ");
        //if the token is indeed a variable declaration,
        if (parts[0].equals("exists") && parts.length >= 2) {
            String[] declaration = parts[1].split("\\.");

            String varId = declaration[0];
            if (declaration[1].startsWith("(")) {
                declaration[1] = declaration[1].substring(1);
            }
            String varName = declaration[1].substring(
                    declaration[1].indexOf("_") + 1,
                    declaration[1].indexOf('('));
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

    /**
     * Renames the duplicate identifiers in a lambda.
     * This makes the lambda easier to read since there is no need to watch for scope information.
     */
    private void renameDuplicateIdentifiers() {
        Scanner sc = new Scanner(parseResult.getLambda());
        sc.useDelimiter("exists");
        String part;
        String identifier;
        usedIdentifiers = new ArrayList<>();
        do {
            part = sc.next();
            identifier = part.trim().split("\\.")[0];
            if (!usedIdentifiers.contains(identifier)) {
                usedIdentifiers.add(identifier);
                usedIdentifiers.sort(Comparator.naturalOrder());
            } else {
                int start = parseResult.getLambda().indexOf(part);
                String subLambda = parseResult.getLambda().substring(start);
                Matcher m = varIdPattern.matcher(subLambda);

                String newId = usedIdentifiers.get(usedIdentifiers.size() - 1);
                while (usedIdentifiers.contains(newId)) {
                    char lastChar = newId.charAt(newId.length() - 1);
                    lastChar++;
                    newId = newId.substring(0, newId.length() - 1) + lastChar;
                }
                while (m.find()) {
                    String found = m.group();
                    subLambda = subLambda.replace(found, found.replace(identifier, newId));
                }
                parseResult.setLambda(parseResult.getLambda().replace(part, subLambda));
                usedIdentifiers.add(newId);
                usedIdentifiers.sort(Comparator.naturalOrder());
            }
        } while (sc.hasNext());
    }

    public void recursiveRenameDuplicateIdentifiers(String lambda) {
        usedIdentifiers = new ArrayList<>();
        Pattern pattern = Pattern.compile("exists");
        Matcher m = pattern.matcher(lambda);
        if (m.find()) {
            int existsIndex = m.start();
            int existsNextIndex = -1;
            if (m.find()) {
                existsNextIndex = m.start();
            }
            if (existsIndex != -1) {
                int firstBracketIndex = lambda.indexOf('(');
                int lastBracketIndex = lambda.lastIndexOf(')');
                String varId = lambda.substring("exists ".length(), firstBracketIndex - 1);
                String scope = lambda.substring(existsIndex, lastBracketIndex);

                if (!usedIdentifiers.contains(varId)) {
                    usedIdentifiers.add(varId);
                    usedIdentifiers.sort(Comparator.naturalOrder());
                } else {
                    Matcher n = varIdPattern.matcher(scope);

                    Character id = varId.charAt(0);
                    Character newId = (char) (id + 1);
                    boolean inScope = true;
                    while (n.find()) {
                        if (n.start() > existsNextIndex) {
                            String nextScope;
                            if (m.find()) {
                                existsNextIndex = m.start();
                            } else {
                                existsNextIndex = Integer.MAX_VALUE;
                            }
                        }
                        String found = n.group();
                        scope = scope.replace(found, found.replace(id, newId));
                    }
                }
            }
        }
    }
}
