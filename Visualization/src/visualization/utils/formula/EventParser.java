package visualization.utils.formula;

import visualization.utils.formula.node.*;

import java.util.*;

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
    /**
     * List of already registered nodes. Used for limiting the recursive depth of the parsing
     */
    private List<BaseNode> registeredNodes;

    /**
     * Package private constructor.
     */
    EventParser() {
    }

    /**
     * Parses a formula.
     *
     * @param lambda   the lambda to parse
     * @param sentence the sentence lambda was created from
     * @return the parsed formula
     */
    @Override
    public Formula parse(String lambda, String sentence) {
        parseResult = new Formula(FormulaParser.simplifyLambda(lambda.trim()), sentence);

        registeredNodes = new ArrayList<>();
        actorNumber = 0;
        eventNumber = 0;
        conjunctionNumber = 0;
        Map<String, BaseNode> nodes = new HashMap<>();

        parseResult = parse(parseResult.getLambda(), nodes);

        return parseResult;
    }

    /**
     * Parses a formula recursively by successive scopes
     *
     * @param lambda the part of the lambda we want to parse
     * @param nodes  the nodes lists
     * @return a Formula containing the parsed information
     */
    public Formula parse(String lambda, Map<String, BaseNode> nodes) {
        List<String> scopes = getScopes(lambda);
        scopes.forEach(scope -> {
            int scopeStartIndex = scope.indexOf('('), scopeEndIndex = getClosingBracketIndex(scope);
            Scanner sc = new Scanner(scope);
            sc.useDelimiter("&");
            String token;
            boolean firstExists = true;

            do {
                String varName;
                String varId;
                token = sc.next().trim();
                if (token.contains("exists")) {
                    if (token.matches(varDeclaration)) {
                        varId = token.split("\\.")[0].split(" ")[1].trim();
                        varName = token.split("\\.")[1].split("\\)")[0].substring(1).split("\\(")[0].trim();
                        if (firstExists) {
                            firstExists = false;
                            BaseNode newNode;
                            if (varId.startsWith("e")) {
                                newNode = new Event(varId, varName);
                                parseResult.getEventEvents().add((Event) newNode);
                            } else {
                                newNode = new Actor(varId, varName);
                                parseResult.getEventActors().add((Actor) newNode);
                            }
                            nodes.put(varId, newNode);
                            registeredNodes.add(newNode);
                        } else if (!registeredNodes.contains(new Actor(varId, varName))) {
                            parse(scope.substring(scopeStartIndex, scopeEndIndex), nodes);
                        }
                    }
                } else if (token.matches(eventSubjectDeclaration)) {
                    registerEventSubject(nodes, nodes, token);
                } else {
                    String[] parts = token.split("\\(");
                    if (parts[0].startsWith("_") && parts.length >= 2) {
                        varId = "c" + conjunctionNumber;
                        varName = parts[0].substring(1);
                        conjunctionNumber++;

                        if (!registeredNodes.contains(new Conjunction(varId, varName))) {
                            String[] joined = parts[1].split("\\)");
                            joined = joined[0].split(",");

                            List<FormulaNode> joinedNodes = new ArrayList<>();
                            for (String s : joined) {
                                FormulaNode node;
                                //its an event
                                if (s.startsWith("e")) node = nodes.get(s);
                                    //its a conjunction
                                else if (s.startsWith("c")) node = nodes.get(s);
                                    //its an actor
                                else node = nodes.get(s);
                                if (node != null)
                                    joinedNodes.add(node);
                            }
                            Conjunction newConj = new Conjunction(varId, varName, joinedNodes.toArray(new FormulaNode[0]));
                            parseResult.getEventConjunctions()
                                    .add(newConj);
                            registeredNodes.add(newConj);
                        }
                    }
                }
            } while (sc.hasNext());
        });
        return parseResult;
    }

    /**
     * Registers an event's subject
     *
     * @param actors the list of actors
     * @param events the list of events
     * @param token  the declaration of the event's subject
     */
    private void registerEventSubject(Map<String, BaseNode> actors, Map<String, BaseNode> events, String token) {
        String varId;
        String[] parts = token.split("\\)");
        if (parts[0].contains("Subj") && parts.length >= 2) {
            varId = parts[0].substring(1);
            varId = varId.substring(varId.indexOf("(") + 1);

            String subjId = parts[1].substring(parts[1].length() - 1);

            Actor subject = (Actor) actors.get(subjId);
            Event event = (Event) events.get(varId);

            event.setSubject(subject);
            event.getActors().add(subject);
        }
    }

    /**
     * Gets the scopes contained in a part of a lambda.
     *
     * @param lambda the part we want to get the scopes from
     * @return a list of all the scopes in string form
     */
    private List<String> getScopes(String lambda) {
        List<String> scopes = new ArrayList<>();
        String subLambda = lambda;
        int nextExistsIndex;
        do {
            nextExistsIndex = subLambda.indexOf("exists");
            if (nextExistsIndex != -1) {
                subLambda = subLambda.substring(nextExistsIndex);
                int endOfScopeIndex = getClosingBracketIndex(subLambda);
                String scope = subLambda.substring(subLambda.indexOf("exists"), endOfScopeIndex + 1);
                scopes.add(scope);
                subLambda = subLambda.substring(endOfScopeIndex + 1);
            }
        } while (nextExistsIndex != -1);
        return scopes;
    }

    /**
     * Get the index of the closing bracket corresponding to the first opening bracket in the str.
     *
     * @param str the string to test
     * @return the index of the closing bracket, or -1 if the string does not contain 2k brackets
     */
    private int getClosingBracketIndex(String str) {
        char[] chars = str.toCharArray();
        int bracketsStartIndex = 0, length = chars.length;
        while (bracketsStartIndex < length) {
            char c = chars[bracketsStartIndex];
            if (c == '(')
                break;
            bracketsStartIndex++;
        }
        if (bracketsStartIndex > length)
            return -1;

        Deque<Character> brackets = new ArrayDeque<>();
        for (int index = bracketsStartIndex; index < length; index++) {
            char c = chars[index];
            if (c == '(')
                brackets.push(c);
            else if (c == ')')
                brackets.pop();
            else continue;
            if (brackets.isEmpty())
                return index;
        }
        return -1;
    }
}
