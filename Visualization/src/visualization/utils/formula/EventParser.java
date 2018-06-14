package visualization.utils.formula;

import javafx.util.Pair;
import visualization.utils.formula.node.*;

import java.util.*;
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
    /**
     * Pattern for the declaration of an events object
     */
    private static final String eventObjectDeclaration = "\\(Acc\\(\\w+\\) = \\w+\\).*";
    /**
     * List of already registered nodes. Used for limiting the recursive depth of the parsing
     */
    private List<FormulaNode> registeredNodes;
    /**
     * Tells whether it is the first time the parser has encountered a variable declaration
     */
    private boolean firstExists;
    /**
     * The number of the current negation
     */
    private int negationNumber;
    /**
     * List of already used variable identifiers
     */
    private List<String> usedIdentifiers;

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
        negationNumber = 0;
        Map<String, FormulaNode> nodes = new HashMap<>();
        usedIdentifiers = new ArrayList<>();

        parseResult = parse(parseResult.getLambda(), nodes, null, null);

        return parseResult;
    }

    /**
     * Parses a formula recursively by successive scopes
     *
     * @param lambda            the part of the lambda we want to parse
     * @param nodes             the nodes lists
     * @param inheritedNegation the negation inheritedNegation from the higher scope
     * @return a Formula containing the parsed information
     */
    private Formula parse(String lambda, Map<String, FormulaNode> nodes, Negation inheritedNegation, Disjunction inheritedDisjunction) {
        List<String> scopes = getScopes(lambda);
        for (String scope : scopes) {
            int scopeStartIndex = scope.indexOf('('), scopeEndIndex = getClosingBracketIndex(scope);

            Negation negation = null;
            if (scope.startsWith("-") || scope.substring(0, scopeStartIndex).endsWith("-")) {
                String negId = "n" + negationNumber;
                negation = new Negation(negId, "");
                negationNumber++;
                if (inheritedNegation != null)
                    inheritedNegation.getNegated().add(negation);
            } else if (inheritedNegation != null) {
                negation = inheritedNegation;
            }

            Disjunction disjunction = inheritedDisjunction;

            Scanner sc = new Scanner(scope);
            sc.useDelimiter("&");
            String token;
            firstExists = true;

            do {
                String varName;
                String varId;
                String otherId;
                token = sc.next().trim();
                if (token.contains("exists")) {
                    if (token.matches(varDeclaration)) {
                        varId = token.split("\\.")[0].split(" ")[1].trim();
                        if (token.contains(",")) {
                            otherId = token.split(",")[1].split("\\)")[0];
                        } else otherId = null;
                        varName = token.split("\\.")[1].split("\\)")[0].substring(1).split("\\(")[0].trim();
                        if (firstExists) {
                            registerVariable(nodes, varName, varId, otherId, negation);
                        } else if (!registeredNodes.contains(new Actor(varId, varName))) {
                            Map<String, FormulaNode> reccNodes = new HashMap<>();
                            nodes.forEach(reccNodes::put);
                            parse(scope.substring(scopeStartIndex, scopeEndIndex), reccNodes, negation, disjunction);
                            reccNodes.forEach((s, formulaNode) -> {
                                if (!nodes.containsKey(s))
                                    nodes.put(s, formulaNode);
                            });
                        }
                    }
                } else if (token.matches(eventSubjectDeclaration)) {
                    registerEventSubject(nodes, token);
                } else if (token.matches(eventObjectDeclaration)) {
                    registerEventObject(nodes, token);
                } else {
                    registerConjunction(nodes, token, negation);
                }
            } while (sc.hasNext());
            if (scope.equals(lambda) && negation != null)
                parseResult.getNegations().add(negation);
        }
        return parseResult;
    }

    private Pair<FormulaNode, FormulaNode> getDisjunctionArgs(String disjunctionScope) {
        if (disjunctionScope.contains("|")) {
            String[] parts = disjunctionScope.split("\\|");
            if (parts.length >= 2) {
                String firstStr;
                String secondStr;
                if (parts[0].contains("&") && parts[1].contains(("&"))) {
                    firstStr = getScopes(parts[0]).get(0).split("&")[0].trim();
                    secondStr = getScopes(parts[1]).get(0).split("&")[0].trim();
                } else {
                    firstStr = parts[0];
                    secondStr = parts[1];
                }
                return null;
            }
        }
        return null;
    }

    private Pair<String, String> getVarIdAndName(String lambda) {
        FormulaNode node;
        String varId;
        String varName;
        if (lambda.contains(".")) {
            varId = lambda.split("\\.")[0].split(" ")[1].trim();
            String otherId;
            if (lambda.contains(",")) {
                otherId = lambda.split(",")[1].split("\\)")[0];
            } else otherId = null;
            varName = lambda.split("\\.")[1].split("\\)")[0].substring(1).split("\\(")[0].trim();
        } else {
            while (lambda.charAt(0) == '(')
                lambda = lambda.substring(1);
            String[] parts = lambda.split("\\(");
            varId = parts[1].substring(0, parts[1].length() - 1);
            varName = parts[0];
        }
        node = varId.startsWith("e") ? new Event(getUnusedIdentifier(varId), varName)
                : new Actor(getUnusedIdentifier(varId), varName);
        return new Pair<>(varId, varName);
    }

    private String getDisjunctionScope(String scope) {
        if (scope.contains("|")) {
            int disjunctionIndex = scope.indexOf('|');
            int endOfDisjunctionScope = getClosingBracketIndex(disjunctionIndex, scope, 1);
            int startOfDisjunctionScope = getOpeningBracketIndex(disjunctionIndex, scope, 1);
            return scope.substring(startOfDisjunctionScope, endOfDisjunctionScope + 1);
        }
        return null;
    }

    private int getOpeningBracketIndex(int endIndex, String str, int offset) {
        char[] chars = str.toCharArray();
        int length = chars.length;
        if (endIndex > length)
            return -1;

        Deque<Character> brackets = new ArrayDeque<>();
        for (int i = 0; i < offset; i++) {
            brackets.push(')');
        }
        for (int index = endIndex; index >= 0; index--) {
            char c = chars[index];
            if (c == ')')
                brackets.push(c);
            else if (c == '(')
                brackets.pop();
            else continue;
            if (brackets.isEmpty())
                return index;
        }
        return -1;
    }

    private int getClosingBracketIndex(int startindex, String str, int offset) {
        char[] chars = str.toCharArray();
        int length = chars.length;

        Deque<Character> brackets = new ArrayDeque<>();
        for (int i = 0; i < offset; i++) {
            brackets.push('(');
        }
        for (int index = startindex; index < length; index++) {
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

    /**
     * Registers a variable. They may be events
     *
     * @param nodes    the list of all nodes
     * @param varName  the name of the variable
     * @param varId    the id of the variable
     * @param negation the negation of the current scope
     * @return the registered node
     */
    private FormulaNode registerVariable(Map<String, FormulaNode> nodes, String varName, String varId, String otherId, Negation negation) {
        firstExists = false;
        FormulaNode newNode;
        if (varId.startsWith("e")) {
            newNode = new Event(getUnusedIdentifier(varId), varName);
            if (otherId != null) {
                Actor object = (Actor) nodes.get(otherId);
                if (object != null)
                    ((Event) newNode).setObject(object);
            }
            parseResult.getEventEvents().add((Event) newNode);
        } else {
            newNode = new Actor(getUnusedIdentifier(varId), varName);
            parseResult.getEventActors().add((Actor) newNode);
        }
        nodes.put(varId, newNode);
        registeredNodes.add(newNode);
        if (negation != null) {
            negation.getNegated().add(newNode);
        }
        return newNode;
    }

    /**
     * Registers an event's object
     *
     * @param nodes the list of all the nodes
     * @param token the declaration of the event's object
     */
    private void registerEventObject(Map<String, FormulaNode> nodes, String token) {
        String varId;
        String[] parts = token.split("\\)");
        if (parts[0].contains("Acc") && parts.length >= 2) {
            varId = parts[0].substring(1);
            varId = varId.substring(varId.indexOf("(") + 1);

            String objId = parts[1].split("=")[1].trim();

            Actor object = (Actor) nodes.get(objId);
            Event event = (Event) nodes.get(varId);
            //the object needs to be set only the first time when descending through recursive calls
            if (event.getObject() == null) {
                event.setObject(object);
                event.getActors().add(object);
            }
        }
    }

    /**
     * Registers an event's subject
     *
     * @param nodes the list of all nodes
     * @param token the declaration of the event's subject
     */
    private void registerEventSubject(Map<String, FormulaNode> nodes, String token) {
        String varId;
        String[] parts = token.split("\\)");
        if (parts[0].contains("Subj") && parts.length >= 2) {
            varId = parts[0].substring(1);
            varId = varId.substring(varId.indexOf("(") + 1);

            String subjId = parts[1].split("=")[1].trim();

            Actor subject = (Actor) nodes.get(subjId);
            Event event = (Event) nodes.get(varId);
            //the subject needs to be set only the first time when descending through recursive calls
            if (event.getSubject() == null) {
                event.setSubject(subject);
                event.getActors().add(subject);
            }
        }
    }

    /**
     * Registers a conjunction.
     *
     * @param nodes    the list of all the nodes
     * @param token    the declaration of the conjuction
     * @param negation the current negation
     */
    private void registerConjunction(Map<String, FormulaNode> nodes, String token, Negation negation) {
        String varId;
        String varName;
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
                Conjunction newConj = new Conjunction(getUnusedIdentifier(varId), varName, joinedNodes.toArray(new FormulaNode[0]));
                parseResult.getEventConjunctions()
                        .add(newConj);
                registeredNodes.add(newConj);
                if (negation != null)
                    negation.getNegated().add(newConj);
            }
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
        Pattern p = Pattern.compile("-?exists");
        int nextExistsIndex;
        do {
            nextExistsIndex = getNextExistsIndex(subLambda, p);
            if (nextExistsIndex != -1) {
                subLambda = subLambda.substring(nextExistsIndex);
                int startOfScopeIndex = getNextExistsIndex(subLambda, p);
                int endOfScopeIndex = getClosingBracketIndex(subLambda);
                String scope = subLambda.substring(startOfScopeIndex, endOfScopeIndex + 1);
                scopes.add(scope);
                subLambda = subLambda.substring(endOfScopeIndex + 1);
            }
        } while (nextExistsIndex != -1);
        return scopes;
    }

    /**
     * Gets the index of the next variable declaration.
     *
     * @param subLambda the lambda
     * @param p         the pattern to search for
     * @return the index of the first declaration
     */
    private int getNextExistsIndex(String subLambda, Pattern p) {
        int nextExistsIndex;
        Matcher m = p.matcher(subLambda);
        boolean found = m.find();
        nextExistsIndex = found ? m.start() : -1;
        return nextExistsIndex;
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

        return getClosingBracketIndex(bracketsStartIndex, str, 0);
    }

    /**
     * Gets an unused variable identifier.
     *
     * @param varId the base identifier
     * @return the new, unused identifier
     */
    private String getUnusedIdentifier(String varId) {
        if (usedIdentifiers.contains(varId)) {
            String base = varId.replaceAll("\\d", "");
            int id = 0;
            do {
                varId = base + id;
                id++;
            } while (usedIdentifiers.contains(varId));
        }
        usedIdentifiers.add(varId);
        return varId;
    }
}
